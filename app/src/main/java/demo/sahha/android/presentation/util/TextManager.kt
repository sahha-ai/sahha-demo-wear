package demo.sahha.android.presentation.util

import java.util.Locale

class TextManager {
    fun capitalizeNoUnderscores(
        text: String,
    ): String {
        return text.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else text.toString()
        }
            .replace('_', ' ')
    }
}