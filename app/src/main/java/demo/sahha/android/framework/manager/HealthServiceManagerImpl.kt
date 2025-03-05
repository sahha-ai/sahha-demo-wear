package demo.sahha.android.framework.manager

import android.content.Context
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseTrackedStatus
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.getCurrentExerciseInfo
import androidx.health.services.client.pauseExercise
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.resumeExercise
import androidx.health.services.client.startExercise
import dagger.hilt.android.qualifiers.ApplicationContext
import demo.sahha.android.domain.manager.HealthServiceManager
import demo.sahha.android.domain.model.ExerciseTypeDto
import demo.sahha.android.domain.model.toExerciseTypeDto
import javax.inject.Inject

private const val EXERCISE_IN_PROGRESS_ERROR = "An exercise is already in progress"
private const val EXERCISE_UNKNOWN_ERROR = "Something went wrong, could not start exercise"
private const val EXERCISE_PREP_ERROR = "Error preparing exercise"
private const val EXERCISE_START_ERROR = "Error starting exercise"

class HealthServiceManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : HealthServiceManager {
    private val healthClient = HealthServices.getClient(context)
    private val exerciseClient = healthClient.exerciseClient

    override suspend fun getExerciseTypeCapabilities(): Set<ExerciseTypeDto> {
        val capabilities = exerciseClient.getCapabilities()
        val types = capabilities.supportedExerciseTypes
        return types.map { it.toExerciseTypeDto() }.toSet()
    }

    override suspend fun getExerciseTypeDto(id: Int): ExerciseTypeDto {
        return ExerciseType.fromId(id).toExerciseTypeDto()
    }

    override suspend fun prepareExercise(
        exerciseId: Int,
    ): String? {
        val dataTypes = setOf(
            DataType.HEART_RATE_BPM,
        )

        val config = WarmUpConfig(
            exerciseType = ExerciseType.fromId(exerciseId),
            dataTypes = dataTypes,
        )

        val info = exerciseClient.getCurrentExerciseInfo()

        return when (info.exerciseTrackedStatus) {
            ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS -> try {
                exerciseClient.prepareExercise(config)
                null
            } catch (e: Exception) {
                e.message ?: EXERCISE_PREP_ERROR
            }

            else -> EXERCISE_PREP_ERROR
        }
    }

    override suspend fun startOrResumeExercise(
        exerciseId: Int,
    ): String? {
        val dataTypes = setOf(
            DataType.HEART_RATE_BPM,
            DataType.HEART_RATE_BPM_STATS,
        )

        val config = ExerciseConfig(
            exerciseType = ExerciseType.fromId(exerciseId),
            dataTypes = dataTypes,
            isAutoPauseAndResumeEnabled = false,
            isGpsEnabled = false,
        )

        val info = exerciseClient.getCurrentExerciseInfo()

        return when (info.exerciseTrackedStatus) {
            ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS -> {
                try {
                    exerciseClient.startExercise(
                        config
                    )
                    null
                } catch (e: Exception) {
                    e.message ?: EXERCISE_START_ERROR
                }
            }

            ExerciseTrackedStatus.OWNED_EXERCISE_IN_PROGRESS -> {
                exerciseClient.resumeExercise()
                null
            }

            ExerciseTrackedStatus.OTHER_APP_IN_PROGRESS -> EXERCISE_IN_PROGRESS_ERROR
            else -> EXERCISE_UNKNOWN_ERROR
        }
    }

    override suspend fun pauseExercise() {
        exerciseClient.pauseExercise()
    }

    override suspend fun finishExercise() {
        exerciseClient.endExercise()
    }

    override suspend fun setUpdateCallback(callback: ExerciseUpdateCallback) {
        exerciseClient.setUpdateCallback(callback)
    }
}