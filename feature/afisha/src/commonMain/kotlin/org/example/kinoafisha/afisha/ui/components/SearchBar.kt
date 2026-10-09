package org.example.kinoafisha.afisha.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.ic_search
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoRadii
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KinoTheme.colors
    val shape = RoundedCornerShape(KinoRadii.Pill)
    Row(
        modifier = modifier
            .height(40.dp)
            .border(1.dp, colors.line, shape)
            .background(colors.bgSoft, shape)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = null,
            tint = colors.textDim,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(16.dp),
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            cursorBrush = SolidColor(colors.accent),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = KinoColors.Text,
                fontSize = 14.sp,
            ),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text("Поиск по названию…", color = colors.textDim, fontSize = 14.sp)
                }
                inner()
            },
        )
        if (query.isNotEmpty()) {
            Text(
                text = "✕",
                color = colors.textDim,
                fontSize = 13.sp,
                modifier = Modifier
                    .clickable(onClick = onClear)
                    .padding(4.dp),
            )
        }
    }
}
