package demo.sahha.android.presentation.exercise_detail

import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseUpdate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.sahha.android.domain.manager.HealthServiceManager
import demo.sahha.android.domain.manager.WearableMessageManager
import demo.sahha.android.presentation.util.TextManager
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

private const val TAG = "ExerciseDetailViewModel"

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val healthManager: HealthServiceManager,
    private val wearableMessageManager: WearableMessageManager,
    private val textManager: TextManager,
) : ViewModel(), ExerciseUpdateCallback {
    var state by mutableStateOf(ExerciseDetailState())
        private set
    private val formatter = DecimalFormat("0.000")

    init {
        state = state.copy(isLoading = true)
    }

    fun trySetExercise(
        id: Int,
        onError: ((e: String) -> Unit)?,
    ) {
        viewModelScope.launch {
            val error = healthManager.prepareExercise(id)
            error?.also { e -> onError?.invoke(e) }
                ?: setExercise(id)
        }
    }

    private suspend fun setExercise(id: Int) {
        val exercise = healthManager.getExerciseTypeDto(id)
        state = state.copy(
            exerciseName = textManager.capitalizeNoUnderscores(exercise.type),
            isLoading = false
        )
    }

    private fun setExerciseStarted(started: Boolean) {
        state = state.copy(exerciseStarted = started)
    }

    private fun setExerciseFinished(finished: Boolean) {
        state = state.copy(exerciseFinished = finished)
    }

    private fun startListeningToExerciseUpdates() {
        viewModelScope.launch {
            healthManager.setUpdateCallback(this@ExerciseDetailViewModel)
            setExerciseStarted(true)
        }
    }

    fun startOrResumeExercise(id: Int, onError: ((e: String) -> Unit)?) {
        viewModelScope.launch {
            val error = healthManager.startOrResumeExercise(id)

            error?.also { e -> onError?.invoke(e) }
                ?: startListeningToExerciseUpdates()
        }
    }

    fun pauseExercise() {
        viewModelScope.launch {
            if (state.exerciseStarted) {
                healthManager.pauseExercise()
                setExerciseStarted(false)
            }
        }
    }

    fun finishExercise() {
        viewModelScope.launch {
            if (state.exerciseStarted) {
                healthManager.finishExercise()
                setExerciseStarted(false)
            }
        }
    }

    private fun getDurationInSeconds(checkpoint: ExerciseUpdate.ActiveDurationCheckpoint?): Double? {
        return checkpoint?.let { cp ->
            val duration = (Instant.now()
                .toEpochMilli() - cp.time.toEpochMilli()) + cp.activeDuration.toMillis()
            duration.toDouble().div(1000)
        }
    }

    private fun setDuration(update: ExerciseUpdate) {
        val seconds = getDurationInSeconds(update.activeDurationCheckpoint)

        try {
            state = state.copy(duration = formatter.format(seconds))
        } catch (e: Exception) {
            Log.d(TAG, e.message ?: "Error formatting seconds")
        }
    }

    private fun setHeartRate(update: ExerciseUpdate) {
        val heartRate = update.latestMetrics.getData(DataType.HEART_RATE_BPM)
        heartRate.also { hr ->
            if (hr.isNotEmpty()) state = state.copy(heartRate = hr.last().value)
        }
    }

    private fun sendExerciseStateToConnectedDevices(update: ExerciseUpdate) {
        val exerciseName = update.exerciseConfig?.exerciseType?.name
        exerciseName?.also { name ->
            wearableMessageManager.sendData(
                label = "name",
                data = textManager.capitalizeNoUnderscores(name).toByteArray()
            )
        }

        val exerciseEnded = update.exerciseStateInfo.state.isEnded
        wearableMessageManager.sendData(
            "ended",
            exerciseEnded.toString().toByteArray()
        )
    }

    private fun sendSummaryToConnectedDevices(update: ExerciseUpdate) {
        val heartRateStats = update.latestMetrics.getData(DataType.HEART_RATE_BPM_STATS)
        heartRateStats?.also { stats ->
            val durationMillis = stats.end.toEpochMilli() - stats.start.toEpochMilli()
            val durationSeconds = durationMillis.toDouble().div(1000)

            wearableMessageManager.sendData(
                "heart/min",
                stats.min.toString().toByteArray()
            )
            wearableMessageManager.sendData(
                "heart/max",
                stats.max.toString().toByteArray()
            )
            wearableMessageManager.sendData(
                "heart/average",
                stats.average.toString().toByteArray()
            )
            wearableMessageManager.sendData(
                label = "duration/start",
                data = stats.start.toString().toByteArray()
            )
            wearableMessageManager.sendData(
                label = "duration/end",
                data = stats.end.toString().toByteArray()
            )
            wearableMessageManager.sendData(
                label = "duration/total",
                data = formatter.format(durationSeconds).toByteArray()
            )
        }
    }

    private fun sendToConnectedDevices(update: ExerciseUpdate) {
        val heartRateList = update.latestMetrics.getData(DataType.HEART_RATE_BPM)
        heartRateList.also { list ->
            if (list.isNotEmpty()) {
                wearableMessageManager.sendData(
                    label = "heart",
                    data = list.last().value.toString().toByteArray()
                )
            }
        }

        val duration = getDurationInSeconds(update.activeDurationCheckpoint)
        duration?.also { d ->
            wearableMessageManager.sendData(
                label = "duration",
                data = d.toString().toByteArray()
            )
        }
    }

    override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
        if (!update.exerciseStateInfo.state.isPaused) {
            setDuration(update)
            setHeartRate(update)
            sendToConnectedDevices(update)
            sendSummaryToConnectedDevices(update)
        }

        if (update.exerciseStateInfo.state.isEnded) {
            sendExerciseStateToConnectedDevices(update)
            setExerciseFinished(true)
        }
    }

    override fun onAvailabilityChanged(dataType: DataType<*, *>, availability: Availability) {}
    override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {}
    override fun onRegistered() {}
    override fun onRegistrationFailed(throwable: Throwable) {}
}