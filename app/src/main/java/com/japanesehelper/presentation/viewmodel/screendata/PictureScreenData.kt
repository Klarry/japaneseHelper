package com.japanesehelper.presentation.viewmodel.screendata

sealed class PictureScreenData

class PictureLoading : PictureScreenData()
class PictureError(val message: String) : PictureScreenData()
class PictureSuccess(val url: String?) : PictureScreenData()
class PictureLimitExceeded : PictureScreenData()