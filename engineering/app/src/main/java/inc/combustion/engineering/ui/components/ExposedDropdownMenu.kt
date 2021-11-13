package inc.combustion.engineering.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import inc.combustion.engineering.R
import inc.combustion.engineering.ui.CombustionAppContent
import inc.combustion.engineering.ui.rememberCombustionAppState
import inc.combustion.engineering.ui.theme.CombustionIncEngineeringTheme

@Composable
fun ExposedDropdownMenu(
    labelText: String,
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onShowMenu: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
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
            modifier = modifier,
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
                ExposedDropdownMenu(
                    labelText = "Label",
                    items= listOf("ITEM1234", "SAMPLEABC"),
                    selectedIndex = 0,
                    onItemSelected = { },
                    onShowMenu = { }
                )
            }
        )
    }
}
