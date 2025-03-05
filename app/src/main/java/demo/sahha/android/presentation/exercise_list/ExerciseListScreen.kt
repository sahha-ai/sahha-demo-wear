package demo.sahha.android.presentation.exercise_list

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import demo.sahha.android.domain.manager.HealthServiceManager
import demo.sahha.android.domain.model.ExerciseTypeDto
import demo.sahha.android.framework.manager.PermissionManager
import demo.sahha.android.presentation.component.OverlayTitle
import demo.sahha.android.presentation.navigation.Screen
import demo.sahha.android.presentation.util.TextManager

@Preview
@Composable
fun ExerciseListScreenPreview() {
    val manager = object : HealthServiceManager {
        override suspend fun getExerciseTypeCapabilities(): Set<ExerciseTypeDto> {
            return setOf(
                ExerciseTypeDto(
                    1,
                    "Test Exercise Type 1"
                ),
                ExerciseTypeDto(
                    2,
                    "Test Exercise Type 2"
                ),
                ExerciseTypeDto(
                    3,
                    "Test Exercise Type 3"
                ),
            )
        }

        override suspend fun getExerciseTypeDto(id: Int): ExerciseTypeDto {
            TODO("Not yet implemented")
        }

        override suspend fun prepareExercise(exerciseId: Int): String? {
            TODO("Not yet implemented")
        }

        override suspend fun startOrResumeExercise(exerciseId: Int): String? {
            TODO("Not yet implemented")
        }

        override suspend fun pauseExercise() {
            TODO("Not yet implemented")
        }

        override suspend fun finishExercise() {
            TODO("Not yet implemented")
        }

        override suspend fun setUpdateCallback(callback: ExerciseUpdateCallback) {
            TODO("Not yet implemented")
        }

    }

    ExerciseListScreen(
        navController = rememberNavController(),
        permissionManager = PermissionManager(LocalActivity.current as ComponentActivity),
        viewModel = ExerciseListViewModel(
            healthManager = manager,
            textManager = TextManager()
        )
    )
}

@Composable
fun ExerciseListScreen(
    navController: NavController,
    permissionManager: PermissionManager,
    viewModel: ExerciseListViewModel = hiltViewModel(),
) {
    val state = viewModel.state

    if (state.isLoading) Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colors.primary)
    }
    else OverlayTitle(
        title = "Select Exercise"
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 25.dp),
        ) {
            items(state.exerciseTypeCapabilities.toList()) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (permissionManager.arePermissionsGranted())
                            navController.navigate(
                                Screen.ExerciseDetailScreen.createRoute(it.id)
                            )
                        else
                            permissionManager.requestPermissions()
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = viewModel.capitalizeNoUnderscores(it.type),
                        overflow = TextOverflow.Visible,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}