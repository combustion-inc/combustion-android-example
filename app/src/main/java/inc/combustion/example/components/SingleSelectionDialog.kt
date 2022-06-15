package inc.combustion.engineering.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SingleSelectDialog(title: String,
                       optionsList: List<String>,
                       defaultSelected: Int,
                       submitButtonText: String,
                       onSubmitButtonClick: (Int) -> Unit,
                       onDismissRequest: () -> Unit) {

    var selectedOption by remember { mutableStateOf(defaultSelected) }

    Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
        Surface(shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = title,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.subtitle2,
                    textAlign = TextAlign.Center
                )
                LazyColumn {
                    items(optionsList) {
                        RadioButtonRow(
                            it,
                            optionsList[selectedOption]
                        ) { selectedValue ->
                            selectedOption = optionsList.indexOf(selectedValue)
                        }
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSubmitButtonClick.invoke(selectedOption)
                        onDismissRequest.invoke()
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = submitButtonText)
                }
            }

        }
    }
}