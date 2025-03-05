package demo.sahha.android.presentation.navigation

sealed class Screen(val route: String) {
    object ExerciseListScreen : Screen("exercise_list")
    object ExerciseDetailScreen : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: Int) = "exercise_detail/$exerciseId"
    }
}