package com.japanesehelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.repository.VocabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabViewModel @Inject constructor(
    private val vocabRepository: VocabRepository
) : ViewModel() {

    private val _randomWord = MutableStateFlow("")
    val randomWord = _randomWord.asStateFlow()

    fun getRandomWord() {
        viewModelScope.launch {
            try {
                _randomWord.value = vocabRepository.getRandomWord().word
            } catch (e: Exception) {
                _randomWord.value = "Error: ${e.message}"
            }
        }
    }
}