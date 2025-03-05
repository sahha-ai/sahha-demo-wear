package demo.sahha.android.domain.model

import androidx.health.services.client.data.ExerciseType

data class ExerciseTypeDto(
    val id: Int = 0,
    val type: String = "",
)

fun ExerciseType.toExerciseTypeDto(): ExerciseTypeDto {
    return ExerciseTypeDto(
        id = id,
        type = name
    )
}
