package com.japanesehelper.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.repository.DescriptionRepository
import com.japanesehelper.domain.repository.ImageSearchRepository
import com.japanesehelper.domain.repository.VocabRepository
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionError
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionLoading
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionState
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionSuccess
import com.japanesehelper.presentation.viewmodel.screendata.HomeState
import com.japanesehelper.presentation.viewmodel.screendata.LevelState
import com.japanesehelper.presentation.viewmodel.screendata.PictureError
import com.japanesehelper.presentation.viewmodel.screendata.PictureLimitExceeded
import com.japanesehelper.presentation.viewmodel.screendata.PictureLoading
import com.japanesehelper.presentation.viewmodel.screendata.PictureState
import com.japanesehelper.presentation.viewmodel.screendata.PictureSuccess
import com.japanesehelper.tools.HttpStatus
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
    private val imageSearchRepository: ImageSearchRepository,
    private val descriptionRepository: DescriptionRepository
) : ViewModel() {

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L

        const val DEMO_LOG_TAG = "ImageSearch"
        const val DEMO_LOG_VIA = "Gemini + Google Image Search"
        val DEMO_LOG_DIVIDER = "─".repeat(40)

        const val DEMO_LOG_TAG_DESCRIPTION = "ImageDescription"
    }

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState

    init { getCardData() }

    fun getCardData() {
        viewModelScope.launch {
            getRandomWord()?.join()
            getPictureData()
            getDescription()
            getJLPTLevel().collect {
                _homeState.value = _homeState.value.copy(levelState = it)
            }
        }
    }

    fun updateLevel(level: LevelState) {
        viewModelScope.launch {
            vocabRepository.updateJLPTLevel(level)
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

    private fun getPictureData(): Job? {
        val query = _homeState.value.randomWord?.meaning
        if (query.isNullOrEmpty()) return null

        _homeState.updatePicture(PictureLoading())

        return viewModelScope.launch {
            try {
                Log.d(DEMO_LOG_TAG, "$DEMO_LOG_TAG  $DEMO_LOG_DIVIDER")
                Log.d(DEMO_LOG_TAG, "\u2192 Searching image for: ${_homeState.value.randomWord?.word}")
                Log.d(DEMO_LOG_TAG, "\u2192 Query: \"$query\"")
                Log.d(DEMO_LOG_TAG, "\u2192 Via: $DEMO_LOG_VIA")

                val imageBytes = imageSearchRepository.getSearchResults(query)?.result
                _homeState.updatePicture(PictureSuccess(imageBytes))
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                when (e) {
                    is HttpException -> {
                        if (e.code() == HttpStatus.TOO_MANY_REQUESTS) {
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

    private fun MutableStateFlow<HomeState>.updatePicture(newPicture: PictureState) {
        this.value = this.value.copy(picture = newPicture)
    }

    private fun getDescription(): Job? {
        val word = _homeState.value.randomWord?.word
        val meaning = _homeState.value.randomWord?.meaning
        if (meaning.isNullOrEmpty()) return null

        _homeState.updateDescription(DescriptionLoading())

        return viewModelScope.launch {
            try {
                Log.d(DEMO_LOG_TAG_DESCRIPTION, DEMO_LOG_DIVIDER)
                Log.d(DEMO_LOG_TAG_DESCRIPTION, "→ Word: $word")
                Log.d(DEMO_LOG_TAG_DESCRIPTION, "→ Meaning: \"$meaning\"")
                Log.d(DEMO_LOG_TAG_DESCRIPTION, "→ POST /description")

                val result = descriptionRepository.getDescription(meaning)

                Log.d(DEMO_LOG_TAG_DESCRIPTION, "← 200 OK")
                Log.d(DEMO_LOG_TAG_DESCRIPTION, "→ Mode: UNCONTROLLED")
                Log.d(DEMO_LOG_TAG_DESCRIPTION, "← Response: ${result.uncontrolled}")
                Log.d(DEMO_LOG_TAG_DESCRIPTION, "→ Mode: CONTROLLED")
                Log.d(DEMO_LOG_TAG_DESCRIPTION, "← Response: ${result.controlled}")
                Log.d(DEMO_LOG_TAG_DESCRIPTION, DEMO_LOG_DIVIDER)

                _homeState.updateDescription(DescriptionSuccess(result.uncontrolled, result.controlled))
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _homeState.updateDescription(DescriptionError(e.message.orEmpty()))
                Log.d("getDescription",
                    "Exception thrown: ${ e.message }, " +
                        "cause: ${ e.cause }, " +
                        "localizedMessage: ${ e.localizedMessage }"
                )
                e.printStackTrace()
            }
        }
    }

    private fun MutableStateFlow<HomeState>.updateDescription(newDescription: DescriptionState) {
        this.value = this.value.copy(description = newDescription)
    }
}
