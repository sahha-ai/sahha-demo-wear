package demo.sahha.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import demo.sahha.android.framework.manager.PermissionManager
import demo.sahha.android.presentation.exercise_detail.ExerciseDetailScreen
import demo.sahha.android.presentation.exercise_list.ExerciseListScreen

private const val EXERCISE_ID = "exerciseId"

@Composable
fun NavGraph(
    permissionManager: PermissionManager,
    startDestination: String = Screen.ExerciseListScreen.route,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.ExerciseListScreen.route) {
            ExerciseListScreen(
                permissionManager = permissionManager,
                navController = navController
            )
        }
        composable(
            route = Screen.ExerciseDetailScreen.route,
            arguments = listOf(navArgument(EXERCISE_ID) { type = NavType.IntType })
        ) { navBackStackEntry ->
            val exerciseId = navBackStackEntry.arguments?.getInt(EXERCISE_ID)
            exerciseId?.also { id ->
                ExerciseDetailScreen(
                    exerciseId = id,
                    navController = navController
                )
            }
        }
    }
}