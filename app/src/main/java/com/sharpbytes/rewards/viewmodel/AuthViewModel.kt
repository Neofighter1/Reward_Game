package com.sharpbytes.rewards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharpbytes.rewards.data.AuthRepository
import com.sharpbytes.rewards.data.User
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> = _currentUser

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUp(email, password, displayName)
            result.onSuccess { uid ->
                _authState.value = AuthState.Success(uid)
            }
            result.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Signup failed")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            result.onSuccess { uid ->
                loadUserData(uid)
                _authState.value = AuthState.Success(uid)
            }
            result.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Login failed")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Idle
        _currentUser.value = null
    }

    private fun loadUserData(uid: String) {
        viewModelScope.launch {
            val result = authRepository.getUserData(uid)
            result.onSuccess { user ->
                _currentUser.value = user
            }
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val uid: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
