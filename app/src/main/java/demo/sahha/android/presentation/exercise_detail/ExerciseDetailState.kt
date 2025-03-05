package demo.sahha.android.presentation.exercise_detail

data class ExerciseDetailState(
    val isLoading: Boolean = false,
    val exerciseName: String = "",
    val duration: String = "0.000",
    val heartRate: Double? = 0.0,
    val exerciseStarted: Boolean = false,
    val exerciseFinished: Boolean = false,
)
