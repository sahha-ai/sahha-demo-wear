package demo.sahha.android.presentation.exercise_list

import demo.sahha.android.domain.model.ExerciseTypeDto

data class ExerciseListState(
    val isLoading: Boolean = false,
    val exerciseTypeCapabilities: Set<ExerciseTypeDto> = emptySet(),
    val selectedExerciseType: ExerciseTypeDto? = null,
)