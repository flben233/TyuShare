package model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import component.tool.SoundStreamMode
import kotlinx.serialization.Serializable
import util.MutableStateSerializer


@Serializable
data class ApplicationSetting(
    @Serializable
    var fileReceivePath: String = ".\\downloads",
    @Serializable
    var defaultOpenWindow: Boolean = true,
    @Serializable(with = MutableStateSerializer::class)
    var soundStreamStatus: MutableState<Boolean> = mutableStateOf(false),
    @Serializable(with = MutableStateSerializer::class)
    var soundStreamMode: MutableState<String> = mutableStateOf(SoundStreamMode.LISTENER),
    @Serializable(with = MutableStateSerializer::class)
    var clipboardStatus: MutableState<Boolean> = mutableStateOf(true),

)