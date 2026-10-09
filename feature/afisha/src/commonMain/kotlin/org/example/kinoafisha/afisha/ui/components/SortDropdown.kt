package org.example.kinoafisha.afisha.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoRadii
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.util.label
import org.example.kinoafisha.core.domain.model.SortOrder

@Composable
fun SortDropdown(
    sort: SortOrder,
    onSortChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
    forFavorites: Boolean = false,
) {
    val colors = KinoTheme.colors
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(KinoRadii.Pill)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Text(
                text = sort.label(forFavorites) + " ▾",
                color = KinoColors.Text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .height(40.dp)
                    .border(1.dp, colors.line, shape)
                    .background(colors.bgSoft, shape)
                    .clickable { expanded = true }
                    .padding(horizontal = 14.dp, vertical = 11.dp),
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(colors.bgSoft),
            ) {
                SortOrder.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.label(forFavorites),
                                color = KinoColors.Text,
                            )
                        },
                        onClick = {
                            expanded = false
                            onSortChange(option)
                        },
                    )
                }
            }
        }
    }
}
