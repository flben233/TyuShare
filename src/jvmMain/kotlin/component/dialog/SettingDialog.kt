package component.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import component.setting.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 动画持续时间
private const val ANIMATION_TIME = 180

/**
 * 设置界面弹窗
 * @author ShirakawaTyu
 * @since 9/17/2023 4:43 PM
 * @version 1.0
 */
@Preview
@Composable
fun SettingDialog(onCloseRequest: () -> Unit) {
    // 等待Dialog加载完毕后再显示AnimatedVisibility的内容，以此来实现进入动画
    val visibility = remember { mutableStateOf(false) }
    LaunchedEffect("setting") {
        visibility.value = true
    }

    Dialog(onDismissRequest = {
        CoroutineScope(Dispatchers.Default).launch {
            exitWithAnimation(visibility) {
                onCloseRequest()
            }
        }
    }) {
        AnimatedVisibility(
            visible = visibility.value,
            enter = fadeIn(animationSpec = tween(ANIMATION_TIME)),
            exit = fadeOut(animationSpec = tween(ANIMATION_TIME))
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxSize().padding(0.dp, 40.dp)
            ) {
                Box {
                    val scrollState = rememberScrollState(0)
                    Column(
                        Modifier.padding(15.dp).verticalScroll(scrollState),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            // 标题
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Settings, "")
                                Spacer(Modifier.width(5.dp))
                                Text("设置", fontWeight = FontWeight.Bold)
                            }

                            Spacer(Modifier.height(10.dp))

                            // 检查更新
                            Updater()
                            // 系统设置区域
                            System()
                            // 文件设置区域
                            FileReceive()
                            // 音频设置区域
                            Audio()
                            // 项目地址
                            Github()
                        }

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(onClick = {
                                CoroutineScope(Dispatchers.Default).launch {
                                    exitWithAnimation(visibility) {
                                        onCloseRequest()
                                    }
                                }
                            }) {
                                Text("关闭")
                            }
                        }
                    }
                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(scrollState),
                        modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

suspend fun exitWithAnimation(
    animateTrigger: MutableState<Boolean>,
    onDismissRequest: () -> Unit
) {
    animateTrigger.value = false
    delay(ANIMATION_TIME.toLong())
    onDismissRequest()
}
