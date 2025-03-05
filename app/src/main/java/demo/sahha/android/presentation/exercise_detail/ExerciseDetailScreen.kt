package demo.sahha.android.presentation.exercise_detail

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import demo.sahha.android.domain.manager.HealthServiceManager
import demo.sahha.android.domain.manager.WearableMessageManager
import demo.sahha.android.domain.model.ExerciseTypeDto
import demo.sahha.android.presentation.component.OverlayTitle
import demo.sahha.android.presentation.util.TextManager

@Preview
@Composable
fun ExerciseDetailScreenPreview() {
    val manager = object : HealthServiceManager {
        override suspend fun getExerciseTypeCapabilities(): Set<ExerciseTypeDto> {
            TODO("Not yet implemented")
        }

        override suspend fun getExerciseTypeDto(id: Int): ExerciseTypeDto {
            return ExerciseTypeDto(1, "EXAMPLE")
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
    val messageManager = object : WearableMessageManager {
        override fun sendData(label: String, data: ByteArray) {
            TODO("Not yet implemented")
        }
    }

    ExerciseDetailScreen(
        exerciseId = 1,
        navController = rememberNavController(),
        viewModel = ExerciseDetailViewModel(
            healthManager = manager,
            wearableMessageManager = messageManager,
            textManager = TextManager()
        )
    )
}

@Composable
fun ExerciseDetailScreen(
    exerciseId: Int,
    navController: NavController,
    viewModel: ExerciseDetailViewModel = hiltViewModel(),
) {
    BackHandler {
        navController.popBackStack()
    }

    val context = LocalContext.current
    val state = viewModel.state

    if (state.isLoading) Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colors.primary)
    }
    else OverlayTitle(
        title = state.exerciseName
    ) {
        ScalingLazyColumn {
            item {
                Row {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                    ) {
                        Icon(
                            painter = rememberVectorPainter(
                                Icons.AutoMirrored.Rounded.ArrowBack
                            ),
                            contentDescription = "Back arrow",
                            tint = MaterialTheme.colors.primary,
                        )
                    }
                    if (state.exerciseStarted)
                        IconButton(
                            onClick = { viewModel.pauseExercise() },
                            colors = IconButtonDefaults.outlinedIconButtonColors(
                                containerColor = MaterialTheme.colors.primary
                            )
                        ) {
                            Icon(
                                painter = rememberVectorPainter(Icons.Rounded.Pause),
                                contentDescription = "Pause icon",
                                tint = MaterialTheme.colors.onPrimary
                            )

                        }
                    else
                        IconButton(
                            onClick = {
                                viewModel.startOrResumeExercise(exerciseId) { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = IconButtonDefaults.outlinedIconButtonColors(
                                containerColor = MaterialTheme.colors.primary
                            )
                        ) {
                            Icon(
                                painter = rememberVectorPainter(Icons.Rounded.PlayArrow),
                                contentDescription = "Play arrow icon",
                                tint = MaterialTheme.colors.onPrimary
                            )
                        }

                    IconButton(
                        onClick = {
                            viewModel.finishExercise()
                        },
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            containerColor = MaterialTheme.colors.error
                        )
                    ) {
                        Icon(
                            painter = rememberVectorPainter(Icons.Rounded.Stop),
                            contentDescription = "Stop icon",
                            tint = MaterialTheme.colors.onError
                        )
                    }
                }

            }

            item {
                Text("${state.duration} seconds")
            }

            item {
                Text("${state.heartRate} bpm")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.trySetExercise(exerciseId) { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(state.exerciseFinished) {
        if (state.exerciseFinished)
            navController.popBackStack()
    }
}