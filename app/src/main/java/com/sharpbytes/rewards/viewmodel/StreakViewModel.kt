package com.sharpbytes.rewards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharpbytes.rewards.data.StreakRepository
import com.sharpbytes.rewards.data.StreakRecord
import kotlinx.coroutines.launch

class StreakViewModel : ViewModel() {
    private val streakRepository = StreakRepository()

    private val _streakDays = MutableLiveData<Int>(0)
    val streakDays: LiveData<Int> = _streakDays

    private val _todayStreak = MutableLiveData<StreakRecord?>(null)
    val todayStreak: LiveData<StreakRecord?> = _todayStreak

    private val _state = MutableLiveData<StreakState>(StreakState.Idle)
    val state: LiveData<StreakState> = _state

    fun completeTask(uid: String, taskId: String) {
        viewModelScope.launch {
            _state.value = StreakState.Loading
            val result = streakRepository.updateStreak(uid, taskId)
            result.onSuccess { streakCount ->
                _streakDays.value = streakCount
                _state.value = StreakState.Success
            }
            result.onFailure { error ->
                _state.value = StreakState.Error(error.message ?: "Failed to update streak")
            }
        }
    }

    fun loadTodayStreak(uid: String) {
        viewModelScope.launch {
            val result = streakRepository.getStreakData(uid)
            result.onSuccess { record ->
                _todayStreak.value = record
            }
        }
    }

    sealed class StreakState {
        object Idle : StreakState()
        object Loading : StreakState()
        object Success : StreakState()
        data class Error(val message: String) : StreakState()
    }
}
