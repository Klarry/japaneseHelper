package com.japanesehelper.presentation.viewmodel.screendata

sealed class DescriptionState

class DescriptionLoading : DescriptionState()
class DescriptionError(val message: String) : DescriptionState()
class DescriptionSuccess(val uncontrolled: String, val controlled: String) : DescriptionState()
