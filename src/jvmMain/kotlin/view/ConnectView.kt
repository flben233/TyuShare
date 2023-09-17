package view

import Navigator
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import currentView
import model.ConnectionItem
import service.ConnectionService
import util.DateUtil
import util.HardwareUtil
import util.JsonUtil


/**
 * 连接界面
 * @author ShirakawaTyu
 * @since 9/17/2023 5:27 PM
 * @version 1.0
 */
@Preview
@Composable
fun ConnectView() {
    var connectBtnText by remember { mutableStateOf("连接") }
    var enable by remember { mutableStateOf(true) }
    var ipAddress by remember { mutableStateOf("") }
    val historyPath = "${System.getProperty("user.home")}\\AppData\\Local\\TyuShare\\history.json"
    val lastConnections = JsonUtil.parseJsonFile(historyPath, mutableListOf<ConnectionItem>())
    var localIp by remember { mutableStateOf(ArrayList<String>()) }

    fun onConnectBtn(ip: String, then: (Boolean) -> Unit = {}) {
        connectBtnText = "正在连接..."
        enable = false
        ConnectionService.connect(ip) {
            if (it) {
                lastConnections.add(ConnectionItem(ip, DateUtil.getDateString()))
                currentView.value = Navigator.MAIN_VIEW
            } else {
                connectBtnText = "连接"
                enable = true
            }
            then(it)
            JsonUtil.toJsonFile(historyPath, lastConnections)
        }
    }

    LaunchedEffect("ip") {
        localIp = HardwareUtil.getNetworkAddress() as ArrayList<String>
    }

    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column(
            modifier = Modifier.fillMaxHeight().weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource("jelly-no-connection.png"),
                contentDescription = "Illustration by Irene M. Ray from Ouch!",
                Modifier.weight(2f)
            )
            Text(color = Color.DarkGray, fontSize = 11.sp, lineHeight = 15.sp, text = "本机IP: ")
            LazyRow {
                items(localIp) {
                    Text(color = Color.DarkGray, fontSize = 11.sp, lineHeight = 15.sp, text = it)
                    Spacer(Modifier.padding(3.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Column(Modifier.weight(1.3f)) {
                TextField(ipAddress, modifier = Modifier.fillMaxWidth(),
                    onValueChange = { ipAddress = it }, label = { Text("目标IP") })
                Spacer(Modifier.padding(10.dp))
                Button(enabled = enable, onClick = {
                    onConnectBtn(ipAddress)
                }) { Text(connectBtnText) }
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text("上次连接")
            LazyColumn {
                items(lastConnections.reversed()) { connection ->
                    ListItem(
                        headlineContent = { Text(connection.ipAddress) },
                        supportingContent = { Text("上次连接: ${connection.lastDate}") },
                        trailingContent = {
                            IconButton({
                                onConnectBtn(connection.ipAddress) {
                                    if (it) lastConnections.remove(connection)
                                }
                            }) { Icon(Icons.Filled.ArrowForward, "连接") }
                        },
                        modifier = Modifier.padding(top = 5.dp).clip(MaterialTheme.shapes.medium),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    )
                }
            }
        }
    }
}

