package model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import component.tool.KeyboardMode
import component.tool.SoundStreamMode
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import util.MutableStateSerializer


@Serializable
data class ApplicationSetting(
    @Serializable(with = MutableStateSerializer::class)
    var fileReceivePath: MutableState<String> = mutableStateOf(".\\downloads"),

    @Serializable(with = MutableStateSerializer::class)
    var launchWithSystem: MutableState<Boolean> = mutableStateOf(false),

    @Serializable(with = MutableStateSerializer::class)
    var defaultOpenWindow: MutableState<Boolean> = mutableStateOf(true),

    @Transient
    var soundStreamStatus: MutableState<Boolean> = mutableStateOf(false),

    @Serializable(with = MutableStateSerializer::class)
    var soundStreamMode: MutableState<String> = mutableStateOf(SoundStreamMode.LISTENER),

    @Transient
    var keyboardShareStatus: MutableState<Boolean> = mutableStateOf(false),

    @Serializable(with = MutableStateSerializer::class)
    var keyboardMode: MutableState<String> = mutableStateOf(KeyboardMode.CONTROLLER),

    @Serializable(with = MutableStateSerializer::class)
    var clipboardStatus: MutableState<Boolean> = mutableStateOf(true),

)