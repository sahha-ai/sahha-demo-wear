package demo.sahha.android.presentation.exercise_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import demo.sahha.android.presentation.util.TextManager
import demo.sahha.android.domain.manager.HealthServiceManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val healthManager: HealthServiceManager,
    private val textManager: TextManager,
) : ViewModel() {
    var state by mutableStateOf(ExerciseListState())
        private set

    init {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            val capabilities = healthManager.getExerciseTypeCapabilities()
            state = state.copy(exerciseTypeCapabilities = capabilities, isLoading = false)
        }
    }

    fun capitalizeNoUnderscores(text: String): String {
        return textManager.capitalizeNoUnderscores(text)
    }
}