package demo.sahha.android.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun OverlayTitle(
    title: String,
    content: @Composable() BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(8f)
                .background(
                    color = MaterialTheme.colors.background,
                ),
            textAlign = TextAlign.Center,
        )
        content()
    }
}