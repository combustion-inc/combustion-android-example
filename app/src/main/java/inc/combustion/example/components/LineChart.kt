/*
 * Project: Combustion Inc. Android Example
 * File: LineChart.kt
 * Author: https://github.com/miwright2
 *
 * MIT License
 *
 * Copyright (c) 2022. Combustion Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package inc.combustion.example.components

import android.graphics.Typeface
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import inc.combustion.example.databinding.LineChartBinding
import inc.combustion.framework.service.LoggedProbeDataPoint
import java.util.*

@Composable
fun MeasurementsLineChart(
    plotData: SnapshotStateList<LoggedProbeDataPoint>,
    plotDataStartTimestamp: MutableState<Date>,
    noDataComposable: @Composable () -> Unit,
    primaryColor: Int = MaterialTheme.colors.primary.toArgb(),
    onPrimaryColor: Int = MaterialTheme.colors.onPrimary.toArgb(),
) {
    val configuration = LocalConfiguration.current
    var initialized = remember{ false }
    val dataCount = derivedStateOf {  plotData.count() }

    if(dataCount.value <= 0) {
        noDataComposable()
    }
    else {
        AndroidViewBinding(
            factory = LineChartBinding::inflate,
            modifier = Modifier
                .height(height = (configuration.screenWidthDp).dp)
                .padding(top = 8.dp),
            update = {
                // initialize chart
                if(!initialized) {
                    InitializeChart(lineChart, primaryColor, onPrimaryColor)
                    initialized = true
                }

                // clearing data from chart
                if(dataCount.value < lineChart.data.dataSets[0].entryCount.toFloat())
                    InitializeChart(lineChart, primaryColor, onPrimaryColor)

                for (index in 0..7)
                    lineChart.data.dataSets[index].clear()

                plotData.forEach { dataPoint ->
                    var index = 0;
                    val elapsedTimestampMinutes = (dataPoint.timestamp.time - plotDataStartTimestamp.value.time) / 60000.0f
                    dataPoint.temperatures.values.forEach {
                        val tempInF = ((it.toFloat() * 1.8) + 32.0).toFloat()
                        val entry = Entry(elapsedTimestampMinutes, tempInF)
                        lineChart.data.addEntry(entry, index)
                        index++
                    }
                }

                lineChart.data.notifyDataChanged()
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()
            }
        )
    }
}

fun InitializeChart(
    chart: LineChart,
    primaryColor: Int,
    onPrimaryColor: Int
) {
    val lineColors = listOf(
        Color.Blue.toArgb(),
        Color.Cyan.toArgb(),
        Color.Green.toArgb(),
        Color.Yellow.toArgb(),
        Color.Magenta.toArgb(),
        Color.Red.toArgb(),
        Color(0xFF, 0xA5, 0x00).toArgb(),
        Color(0xFF, 0x00, 0x7F).toArgb()
    )

    // enable description text
    chart.description.isEnabled = true

    // enable touch gestures
    chart.setTouchEnabled(true)

    // enable scaling and dragging
    chart.isDragEnabled = true
    chart.setScaleEnabled(true)
    chart.setDrawGridBackground(false)
    chart.setGridBackgroundColor(primaryColor)

    // if disabled, scaling can be done on x- and y-axis separately
    chart.setPinchZoom(true)

    chart.setBackgroundColor(Color.Transparent.toArgb())

    // add empty data
    val data = LineData()
    data.setValueTextColor(onPrimaryColor)
    data.isHighlightEnabled = false
    chart.data = data

    // modify the legend ...
    chart.legend.form = Legend.LegendForm.LINE
    chart.legend.typeface = Typeface.SANS_SERIF
    chart.legend.textColor = onPrimaryColor

    // setup axis
    chart.xAxis.typeface = Typeface.SANS_SERIF
    chart.xAxis.textColor = onPrimaryColor
    chart.xAxis.setAvoidFirstLastClipping(true)
    chart.xAxis.isEnabled = true

    chart.axisLeft.typeface = Typeface.SANS_SERIF
    chart.axisLeft.textColor = onPrimaryColor
    chart.axisLeft.setDrawGridLines(true)

    chart.axisRight.isEnabled = false

    for (index in 0..7) {
        var set = LineDataSet(null, "T${index+1}")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = lineColors[index]
        set.lineWidth = 2.0f
        set.fillAlpha = 65
        set.fillColor = lineColors[index]
        set.valueTextColor = onPrimaryColor
        set.setDrawCircles(false)
        set.setDrawHighlightIndicators(false)
        set.setDrawVerticalHighlightIndicator(false)
        data.addDataSet(set)
    }
}