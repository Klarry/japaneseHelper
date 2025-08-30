package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.RandomWord

data class HomeState(
    val randomWord: RandomWord? = null,
    val picture: PictureState? = null
)
