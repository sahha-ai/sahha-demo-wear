package demo.sahha.android.domain.manager

import androidx.health.services.client.ExerciseUpdateCallback
import demo.sahha.android.domain.model.ExerciseTypeDto

interface HealthServiceManager {
    suspend fun getExerciseTypeCapabilities(): Set<ExerciseTypeDto>
    suspend fun getExerciseTypeDto(id: Int): ExerciseTypeDto
    suspend fun prepareExercise(exerciseId: Int): String?
    suspend fun startOrResumeExercise(exerciseId: Int): String?
    suspend fun pauseExercise()
    suspend fun finishExercise()
    suspend fun setUpdateCallback(callback: ExerciseUpdateCallback)
}