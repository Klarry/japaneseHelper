package com.japanesehelper.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.repository.GoogleSearchRepository
import com.japanesehelper.domain.repository.VocabRepository
import com.japanesehelper.presentation.viewmodel.screendata.HomeState
import com.japanesehelper.presentation.viewmodel.screendata.LevelState
import com.japanesehelper.presentation.viewmodel.screendata.PictureError
import com.japanesehelper.presentation.viewmodel.screendata.PictureLimitExceeded
import com.japanesehelper.presentation.viewmodel.screendata.PictureLoading
import com.japanesehelper.presentation.viewmodel.screendata.PictureState
import com.japanesehelper.presentation.viewmodel.screendata.PictureSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class VocabViewModel @Inject constructor(
    private val vocabRepository: VocabRepository,
    private val searchRepository: GoogleSearchRepository
) : ViewModel() {

    private companion object {
        const val TOO_MANY_REQUESTS = 429
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState

    init { getCardData() }

    fun getCardData() {
        viewModelScope.launch {
            getRandomWord()?.join()
            getJLPTLevel().collect {
                _homeState.value = _homeState.value.copy(levelState = it)
            }
        }
    }

    private fun getJLPTLevel(): StateFlow<LevelState> {
        return vocabRepository.getJLPTLevel()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = LevelState.RANDOM,
            )
    }

    private fun getRandomWord(): Job? {
        return viewModelScope.launch {
            try {
                _homeState.value = _homeState.value.copy(randomWord = vocabRepository.getRandomWord())
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                Log.d("getRandomWord",
                    "Exception thrown: ${ e.message }, " +
                        "cause: ${ e.cause }, " +
                        "localizedMessage: ${ e.localizedMessage }"
                )
                e.printStackTrace()
            }
        }
    }

    private fun getPictureData(
        apiKey: String = "",
        cx: String = "",
    ): Job? {
        if (apiKey.isEmpty() || cx.isEmpty()) return null

        _homeState.updatePicture(PictureLoading())

        return viewModelScope.launch {
            try {
                val searchResult = getSearchResultAsync(
                    apiKey = apiKey,
                    cx = cx,
                    meaning = _homeState.value.randomWord?.meaning.orEmpty()
                )
                _homeState.updatePicture(PictureSuccess(searchResult))
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                when (e) {
                    is HttpException -> {
                        if (e.code() == TOO_MANY_REQUESTS) {
                            _homeState.updatePicture(PictureLimitExceeded())
                        } else {
                            _homeState.updatePicture(PictureError(e.message.orEmpty()))
                        }
                    }
                    else -> _homeState.updatePicture(PictureError(e.message.orEmpty()))
                }
                Log.d("getPictureData",
                    "Exception thrown: ${ e.message }, " +
                            "cause: ${ e.cause }, " +
                            "localizedMessage: ${ e.localizedMessage }"
                )
                e.printStackTrace()
            }
        }
    }

    private suspend fun getSearchResultAsync(
        apiKey: String,
        cx: String,
        meaning: String
    ): String? {
        return searchRepository.getSearchResults(
            apiKey = apiKey,
            cx = cx,
            query = meaning
        )?.result
    }

    private fun MutableStateFlow<HomeState>.updatePicture(newPicture: PictureState) {
        this.value = this.value.copy(picture = newPicture)
    }
}
