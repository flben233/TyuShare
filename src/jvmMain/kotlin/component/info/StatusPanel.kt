package component.info

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import component.material.Axis
import component.material.DoubleLineChart
import component.material.TextWithTitle
import service.ConnectionService
import util.HardwareUtil
import java.util.Timer
import java.util.TimerTask

@Composable
fun StatusPanel() {
    var cpuLoad by remember { mutableStateOf("0%") }
    var cpuCount by remember { mutableStateOf("12 线程") }
    var memUsage by remember { mutableStateOf("0%") }
    var battery by remember { mutableStateOf("未在充电") }
    val cpuData = remember { mutableListOf<Float>() }
    val memData = remember { mutableListOf<Float>() }

    Timer().schedule(
        object : TimerTask() {
            override fun run() {
                cpuLoad = HardwareUtil.getCpuLoad()
                cpuCount = "${HardwareUtil.getCpuThreadCount()} 线程"
                memUsage = HardwareUtil.getMemoryUsage()
                battery = HardwareUtil.isCharging()
                cpuData.add(HardwareUtil.getCpuLoadFloat())
                memData.add(HardwareUtil.getMemoryUsageFloat())
            }
        }, 0, 1000)

    Card(Modifier.fillMaxSize()) {
        Column(Modifier.padding(15.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("系统状态", fontWeight = FontWeight.Bold)
                Spacer(Modifier.padding(5.dp))
                TextWithTitle("CPU", "$cpuLoad  $cpuCount")
                TextWithTitle("内存", memUsage)
                TextWithTitle("电池", battery)
            }
            Spacer(Modifier.height(10.dp))
            DoubleLineChart(
                primaryAxis = Axis(cpuData, MaterialTheme.colorScheme.primary, "CPU"),
                secondaryAxis = Axis(memData, MaterialTheme.colorScheme.inversePrimary, "内存"),
                background = MaterialTheme.colorScheme.background
            )

            Column {
                Button(onClick = { ConnectionService.disconnect() }, modifier = Modifier.fillMaxWidth()) {
                    Text("断开连接")
                }
                Spacer(Modifier.height(15.dp))
            }
        }
    }
}


