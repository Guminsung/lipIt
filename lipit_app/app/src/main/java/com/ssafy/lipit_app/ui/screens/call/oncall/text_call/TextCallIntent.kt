package com.ssafy.lipit_app.ui.screens.call.oncall.text_call

sealed class TextCallIntent{
    object ToggleTranslation : TextCallIntent()
    data class UpdateInputText(val text: String) : TextCallIntent()
    data object SendMessage : TextCallIntent()
}