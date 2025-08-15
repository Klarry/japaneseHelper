package com.japanesehelper.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.model.RandomWord
import com.japanesehelper.domain.repository.GoogleSearchRepository
import com.japanesehelper.domain.repository.VocabRepository
import com.japanesehelper.presentation.viewmodel.VocabViewModel.HttpStatus.TOO_MANY_REQUESTS
import com.japanesehelper.presentation.viewmodel.screendata.PictureError
import com.japanesehelper.presentation.viewmodel.screendata.PictureLimitExceeded
import com.japanesehelper.presentation.viewmodel.screendata.PictureLoading
import com.japanesehelper.presentation.viewmodel.screendata.PictureScreenData
import com.japanesehelper.presentation.viewmodel.screendata.PictureSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class VocabViewModel @Inject constructor(
    private val vocabRepository: VocabRepository,
    private val searchRepository: GoogleSearchRepository
) : ViewModel() {

    private val _randomWord = MutableStateFlow<RandomWord?>(null)
    val randomWord = _randomWord.asStateFlow()

    private val _pictureData = MutableStateFlow<PictureScreenData?>(null)
    val pictureData = _pictureData.asStateFlow()

    init {
        getRandomWord()
    }

    fun getRandomWord(
        apiKey: String = "",
        cx: String = "",
    ) {
        _pictureData.value = PictureLoading()

        viewModelScope.launch {

            try {
                _randomWord.value = vocabRepository.getRandomWord()

                val searchResult = getSearchResultAsync(
                    apiKey = apiKey,
                    cx = cx,
                    meaning = randomWord.value?.meaning.orEmpty()
                )
                _pictureData.value = PictureSuccess(searchResult)
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        if (e.code() == TOO_MANY_REQUESTS) {
                            _pictureData.value = PictureLimitExceeded()
                        } else {
                            _pictureData.value = PictureError(e.message.orEmpty())
                        }
                    }
                    else -> _pictureData.value = PictureError(e.message.orEmpty())
                }
                Log.d("getRandomWord", "Exception thrown: ${ e.message }, cause: ${ e.cause }, localizedMessage: ${ e.localizedMessage }")
                e.printStackTrace()
            }
        }
    }

    object HttpStatus {
        const val TOO_MANY_REQUESTS = 429
    }

    suspend fun getSearchResultAsync(
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
}