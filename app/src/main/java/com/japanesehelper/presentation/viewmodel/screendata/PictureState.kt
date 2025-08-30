package com.japanesehelper.presentation.viewmodel.screendata

sealed class PictureState

class PictureLoading : PictureState()
class PictureError(val message: String) : PictureState()
class PictureSuccess(val url: String?) : PictureState()
class PictureLimitExceeded : PictureState()
