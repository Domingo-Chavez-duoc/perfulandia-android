package com.domichav.perfulandia.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domichav.perfulandia.ui.theme.Primary

enum class ButtonVariant { Solid, Outline, Tonal }
enum class ButtonSize { Sm, Default, Lg }

@Composable
fun ShadcnButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Solid,
    size: ButtonSize = ButtonSize.Default,
    enabled: Boolean = true
) {
    val paddings = when (size) {
        ButtonSize.Sm -> Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ButtonSize.Default -> Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ButtonSize.Lg -> Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
    }

    val content = @Composable {
        Box(paddings) {
            Text(text = text, fontWeight = FontWeight.SemiBold)
        }
    }

    when (variant) {
        ButtonVariant.Solid -> Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) { content() }

        ButtonVariant.Outline -> OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            border = BorderStroke(1.dp, Primary)
        ) { content() }

        ButtonVariant.Tonal -> ElevatedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
        ) { content() }
    }
}