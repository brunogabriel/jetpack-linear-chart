package io.github.brunogabriel.jetpacklinearchart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import io.github.brunogabriel.jetpacklinearchart.ui.theme.JetpackLinearChartTheme
import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackLinearChartTheme {
                Surface(color = MaterialTheme.colors.background) {
                    LinearChartApplication()
                }
            }
        }
    }
}

sealed class LinearChartStyle {
    object Default : LinearChartStyle()
    object Smooth : LinearChartStyle()
}

@Composable
fun LinearChart(
    modifier: Modifier = Modifier,
    style: LinearChartStyle = LinearChartStyle.Default,
    data: List<Int>
) {
    Canvas(modifier = modifier) {
        // distance between each x point
        val distance = size.width / (data.size + 1)
        var currentX = 0F
        val maxValue = data.maxOrNull() ?: 0
        val points = mutableListOf<PointF>()

        data.forEachIndexed { index, currentData ->
            if (data.size >= index + 2) {
                val y0 = (maxValue - currentData) * (size.height / maxValue)
                val x0 = currentX + distance
                points.add(PointF(x0, y0))
                currentX += distance
            }
        }

        if (style == LinearChartStyle.Default) {
            for (i in 0 until points.size - 1) {
                drawLine(
                    start = Offset(points[i].x, points[i].y),
                    end = Offset(points[i + 1].x, points[i + 1].y),
                    color = Color(255, 0, 0),
                    strokeWidth = 8f
                )
            }
        } else {
            val cubicPoints1 = mutableListOf<PointF>()
            val cubicPoints2 = mutableListOf<PointF>()

            for (i in 1 until points.size) {
                cubicPoints1.add(PointF((points[i].x + points[i - 1].x) / 2, points[i - 1].y))
                cubicPoints2.add(PointF((points[i].x + points[i - 1].x) / 2, points[i].y))
            }

            val path = Path()
            path.moveTo(points.first().x, points.first().y)

            for (i in 1 until points.size) {
                path.cubicTo(
                    cubicPoints1[i - 1].x,
                    cubicPoints1[i - 1].y,
                    cubicPoints2[i - 1].x,
                    cubicPoints2[i - 1].y,
                    points[i].x,
                    points[i].y
                )
            }

            drawPath(path, color = Color(255, 0, 0), style = Stroke(width = 8f))
        }
    }
}

@Composable
fun LinearChartApplication() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(16.dp),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize(align = Alignment.BottomStart)
        ) {
            LinearChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                data = provideMockData(),
                style = LinearChartStyle.Smooth
            )
        }
    }
}

private fun provideMockData() = listOf(
    5929, 6898, 8961, 5674, 7122, 6592, 3427, 5520, 4680, 7418,
    4743, 4080, 3611, 7295, 9900, 12438, 11186, 5439, 4227, 5138,
    11115, 8386, 12450, 10411, 10852, 7782, 7371, 4983, 9234, 6847
)
