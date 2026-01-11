package com.sharpbytes.rewards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharpbytes.rewards.data.LeaderboardEntry
import com.sharpbytes.rewards.data.LeaderboardRepository
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {
    private val leaderboardRepository = LeaderboardRepository()

    private val _leaderboard = MutableLiveData<List<LeaderboardEntry>>(emptyList())
    val leaderboard: LiveData<List<LeaderboardEntry>> = _leaderboard

    private val _userRank = MutableLiveData<Int>(0)
    val userRank: LiveData<Int> = _userRank

    private val _state = MutableLiveData<LeaderboardState>(LeaderboardState.Idle)
    val state: LiveData<LeaderboardState> = _state

    fun loadLeaderboard() {
        viewModelScope.launch {
            _state.value = LeaderboardState.Loading
            val result = leaderboardRepository.getLeaderboard(50)
            result.onSuccess { entries ->
                _leaderboard.value = entries
                _state.value = LeaderboardState.Success
            }
            result.onFailure { error ->
                _state.value = LeaderboardState.Error(error.message ?: "Failed to load leaderboard")
            }
        }
    }

    fun loadUserRank(uid: String) {
        viewModelScope.launch {
            val result = leaderboardRepository.getUserRank(uid)
            result.onSuccess { rank ->
                _userRank.value = rank
            }
        }
    }

    sealed class LeaderboardState {
        object Idle : LeaderboardState()
        object Loading : LeaderboardState()
        object Success : LeaderboardState()
        data class Error(val message: String) : LeaderboardState()
    }
}
