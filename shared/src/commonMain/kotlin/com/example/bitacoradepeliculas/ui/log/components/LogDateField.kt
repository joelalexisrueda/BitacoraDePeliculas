package com.example.bitacoradepeliculas.ui.log.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bitacoradepeliculas.domain.util.DatePickerUtils
import com.example.bitacoradepeliculas.domain.util.DateFormatter
import com.example.bitacoradepeliculas.domain.util.TodayProvider
import com.example.bitacoradepeliculas.ui.components.AppIcons
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDateField(
    date: LocalDate,
    todayProvider: TodayProvider,
    isDatePickerVisible: Boolean,
    onDatePickerVisibilityChanged: (Boolean) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = DateFormatter.formatToSpanish(date)
    val todayMillis = DatePickerUtils.localDateToUtcMillis(todayProvider.today())

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = DatePickerUtils.localDateToUtcMillis(date),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= todayMillis
            }
            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = todayProvider.today().year
                return year <= currentYear
            }
        }
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onDatePickerVisibilityChanged(true) }
    ) {
        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Fecha de registro") },
            leadingIcon = {
                Icon(
                    imageVector = AppIcons.Calendar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (isDatePickerVisible) {
        DatePickerDialog(
            onDismissRequest = { onDatePickerVisibilityChanged(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = DatePickerUtils.utcMillisToLocalDate(millis)
                            onDateSelected(selectedDate)
                        }
                        onDatePickerVisibilityChanged(false)
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDatePickerVisibilityChanged(false) }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
