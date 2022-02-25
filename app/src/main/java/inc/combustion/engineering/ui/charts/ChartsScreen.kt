package inc.combustion.engineering.ui.charts

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import inc.combustion.engineering.R
import inc.combustion.engineering.databinding.LineChartBinding
import inc.combustion.engineering.ui.CombustionAppContent
import inc.combustion.engineering.ui.CombustionAppState
import inc.combustion.engineering.ui.components.ExposedDropdownMenu
import inc.combustion.engineering.ui.rememberCombustionAppState
import inc.combustion.engineering.ui.theme.CombustionIncEngineeringTheme
import inc.combustion.service.DeviceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun ChartsScreen(
    appState: CombustionAppState
) {
    val viewModel: ChartsViewModel = viewModel(factory = ChartsViewModel.Factory(DeviceManager.instance))
    val state = remember { viewModel.uiState }

    ChartsContent(
        appState = appState,
        state = state
    )
}

@Composable
fun ChartsContent(
    appState: CombustionAppState,
    state: ChartsScreenState
) {
    val isPlaying = rememberSaveable { mutableStateOf(true) }

    Column {
        if(state.deviceSerialNumbers.size > 0) {
            Row(
                horizontalArrangement = Arrangement.Center
            ){
                Box(
                    modifier = Modifier.weight(3.0f)
                ) {
                    ExposedDropdownMenu(
                        labelText = "Probe",
                        items = state.deviceSerialNumbers,
                        selectedIndex = state.selectedIndex.value,
                        onItemSelected = { index ->
                            state.clearChartData.value = true
                            state.onDeviceSelectionChange(index)
                        },
                        onShowMenu = state.onShowMenu
                    )
                }
                TextButton(
                    onClick = {
                        isPlaying.value = !isPlaying.value
                    },
                    modifier = Modifier
                        .weight(1.0f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = if(isPlaying.value) "Pause" else "Play",
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.onPrimary,
                    )
                }
            }
            ProbeChart(state, isPlaying)
        }
        else {
            ProgressWithText(message = appState.noDevicesReasonString)
        }
    }
}

@Composable
fun ProbeChart(
    @Suppress("UNUSED_PARAMETER") state: ChartsScreenState,
    isPlaying: MutableState<Boolean>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.large_padding),
                vertical = dimensionResource(id = R.dimen.small_padding)
            ),
        shape = RoundedCornerShape(
            dimensionResource(id = R.dimen.rounded_corner)
        )
    ) {
        val lineColors = listOf(
            Color.Blue.toArgb(), Color.Cyan.toArgb(), Color.Green.toArgb(),
            Color.Yellow.toArgb(), Color.Magenta.toArgb(), Color.Red.toArgb(),
            Color(0xFF, 0xA5, 0x00).toArgb(),
            Color(0xFF, 0x00, 0x7F).toArgb()
        )

        val primaryColor = MaterialTheme.colors.primary.toArgb()
        val onPrimaryColor = MaterialTheme.colors.onPrimary.toArgb()

        /*
        val lifecycleOwner = LocalLifecycleOwner.current
        val logFlow = remember{ state.getDataFlow(state.selectedIndex.value) }
        val logFlowLifecycleAware = remember(logFlow, lifecycleOwner) {
            logFlow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
        }

        val logEntry = logFlowLifecycleAware.collectAsState(null)
         */

        val chartValue = remember{ mutableStateOf(75.0f) }
        val values = remember { mutableStateListOf<Float>() }
        val timerCallback: () -> Unit = {
            chartValue.value += 0.25f
            values.add(chartValue.value)
        }

        LaunchedEffect(key1 = chartValue, key2 = timerCallback) {
            while(isActive) {
                delay(500L)
                timerCallback()
            }
        }

        LineChart(values, chartValue, lineColors, primaryColor, onPrimaryColor, isPlaying)
    }
}

/**
 * Based on Realtime Line Chart Example for MPAndroid found here:
 * https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/main/java/com/xxmassdeveloper/mpchartexample/RealtimeLineChartActivity.java
 */
@Composable
fun LineChart(
    @Suppress("UNUSED_PARAMETER") values: SnapshotStateList<Float>,
    chartValue: State<Float>,
    @Suppress("UNUSED_PARAMETER") lineColors: List<Int>,
    primaryColor: Int,
    onPrimaryColor: Int,
    isPlaying: MutableState<Boolean>
) {
    var initialized = remember{ false }

    AndroidViewBinding(
        factory = LineChartBinding::inflate,
        update = {
            if(!initialized) {
                initializeChart(lineChart, primaryColor, onPrimaryColor)
                initialized = true
            }
            if(isPlaying.value) {
                for (index in 0..7) {
                    val xAxis = lineChart.data.dataSets[index].entryCount.toFloat()
                    val entry = Entry(xAxis, chartValue.value + index.toFloat())
                    lineChart.data.addEntry(entry, index)
                    lineChart.data.notifyDataChanged()
                }
                lineChart.data.notifyDataChanged()
                lineChart.notifyDataSetChanged()
                lineChart.fitScreen()
                lineChart.invalidate()
            }
        }
    )
}

fun initializeChart(chart: LineChart, primaryColor: Int, onPrimaryColor: Int) {
    val lineColors = listOf(
        Color.Blue.toArgb(), Color.Cyan.toArgb(), Color.Green.toArgb(),
        Color.Yellow.toArgb(), Color.Magenta.toArgb(), Color.Red.toArgb(),
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
    chart.setDrawGridBackground(true)
    chart.setGridBackgroundColor(primaryColor)

    // if disabled, scaling can be done on x- and y-axis separately
    chart.setPinchZoom(true)

    chart.setBackgroundColor(primaryColor)

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

@Composable
fun ProgressWithText(
    message: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LinearProgressIndicator(
            color = MaterialTheme.colors.onPrimary,
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    horizontal = dimensionResource(id = R.dimen.large_padding),
                    vertical = dimensionResource(id = R.dimen.small_padding)
                ),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.subtitle2,
            text = message
        )
    }
}


@Composable
fun ExposedDropdownMenu(
    labelText: String,
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onShowMenu: () -> Unit
) {
    var index by remember { mutableStateOf(selectedIndex) }
    var expanded by remember { mutableStateOf(false) }
    var menuSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.Clear
    else
        Icons.Filled.ArrowDropDown

    Box(
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.large_padding),
            )
            .onGloballyPositioned { layoutCoordinates ->
                menuSize = layoutCoordinates.size.toSize()
            }
        ,
    ) {
        OutlinedTextField(
            value = items[index],
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth(),
            label = {
                Text(
                    text = labelText,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onPrimary
                )
            },
            trailingIcon = {
                Icon(icon,"contentDescription", Modifier.clickable { expanded = !expanded })
            },
            textStyle = MaterialTheme.typography.subtitle2,
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onPrimary
            ),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colors.primary)
                .width(with(LocalDensity.current) { menuSize.width.toDp() })
        ) {
            items.forEachIndexed { selectedIndex, selectedItem ->
                DropdownMenuItem(
                    modifier = Modifier
                        .background(MaterialTheme.colors.primary),
                    onClick = {
                        onItemSelected(selectedIndex)
                        index = selectedIndex
                        expanded = false
                    }
                ) {
                    Text(
                        text = selectedItem,
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.onPrimary,
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable(onClick = {
                    onShowMenu()
                    expanded = true
                })
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    CombustionIncEngineeringTheme {
        val appState = rememberCombustionAppState()
        CombustionAppContent(
            appState = appState,
            content = @Composable {
                ChartsScreen(appState = appState)
            }
        )
    }
}
