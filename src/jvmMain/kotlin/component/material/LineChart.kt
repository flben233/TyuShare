package component.material

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp


data class Axis(
    val data: List<Float>,
    val color: Color,
    val name: String
)

@Composable
fun DoubleLineChart(
    primaryAxis: Axis,
    secondaryAxis: Axis,
    background: Color,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column {
        Surface(color = background, shape = CardDefaults.shape, modifier = modifier.weight(9f)) {
            Canvas(modifier = modifier) {
                fun drawChart(data: List<Float>, color: Color) {
                    var chartData = data
                    if (data.size > 101) {
                        chartData = data.subList(data.size - 101, data.size)
                    }
                    val path = Path()
                    val fillPath = Path()
                    fillPath.moveTo(0f, size.height)
                    for (i in chartData.indices) {
                        if (i == 0) {
                            path.moveTo(0f, (1 - chartData[0]) * size.height)
                        }
                        path.lineTo(i.toFloat() / 100 * size.width, (1 - chartData[i]) * size.height)
                        fillPath.lineTo(i.toFloat() / 100 * size.width, (1 - chartData[i]) * size.height)
                    }
                    fillPath.lineTo((chartData.size.toFloat() - 1) / 100 * size.width, size.height)
                    drawPath(path, color, style = Stroke(2f))
                    drawPath(fillPath, Color(color.red, color.green, color.blue, 0.5f, color.colorSpace))
                }

                for (i in 1..10) {
                    drawLine(
                        Color.LightGray,
                        Offset(0f, i.toFloat() / 10 * size.height),
                        Offset(size.width, i.toFloat() / 10 * size.height)
                    )
                    drawLine(
                        Color.LightGray,
                        Offset(i.toFloat() / 10 * size.width, 0f),
                        Offset(i.toFloat() / 10 * size.width, size.height)
                    )
                }
                drawChart(secondaryAxis.data, secondaryAxis.color)
                drawChart(primaryAxis.data, primaryAxis.color)
            }
        }
        Spacer(Modifier.height(5.dp))
        Row(
            modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChartTag(primaryAxis.color, primaryAxis.name)
            Spacer(Modifier.width(5.dp))
            ChartTag(secondaryAxis.color, secondaryAxis.name)
        }
    }
}

@Composable
fun ChartTag(color: Color, name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.height(7.dp)) {
            drawRect(color, Offset(0f, 0f), size = Size(10.dp.toPx(), 10.dp.toPx()))
        }
        Spacer(Modifier.width(15.dp))
        Text(name)
    }
}