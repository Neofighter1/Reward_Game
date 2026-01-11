package com.sharpbytes.rewards.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharpbytes.rewards.data.GameRepository
import com.sharpbytes.rewards.data.GameResult
import com.sharpbytes.rewards.data.QuizQuestion
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val gameRepository = GameRepository()

    private val _questions = MutableLiveData<List<QuizQuestion>>(emptyList())
    val questions: LiveData<List<QuizQuestion>> = _questions

    private val _gameState = MutableLiveData<GameState>(GameState.Idle)
    val gameState: LiveData<GameState> = _gameState

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int> = _score

    fun startGame() {
        viewModelScope.launch {
            _gameState.value = GameState.Loading
            val result = gameRepository.getQuizQuestions(5)
            result.onSuccess { questions ->
                _questions.value = questions
                _gameState.value = GameState.Playing
            }
            result.onFailure { error ->
                _gameState.value = GameState.Error(error.message ?: "Failed to load questions")
            }
        }
    }

    fun submitAnswer(questionIndex: Int, selectedAnswer: String, isCorrect: Boolean) {
        val current = _score.value ?: 0
        if (isCorrect) {
            _score.value = current + 10
        }
    }

    fun finishGame(uid: String, timeTaken: Long, correctAnswers: Int) {
        viewModelScope.launch {
            val result = _questions.value ?: emptyList()
            val gameResult = GameResult(
                uid = uid,
                score = _score.value ?: 0,
                questionsAttempted = result.size,
                correctAnswers = correctAnswers,
                pointsEarned = (_score.value ?: 0),
                timeTaken = timeTaken
            )
            val saveResult = gameRepository.saveGameResult(gameResult)
            saveResult.onSuccess {
                _gameState.value = GameState.Finished
            }
            saveResult.onFailure { error ->
                _gameState.value = GameState.Error(error.message ?: "Failed to save result")
            }
        }
    }

    sealed class GameState {
        object Idle : GameState()
        object Loading : GameState()
        object Playing : GameState()
        object Finished : GameState()
        data class Error(val message: String) : GameState()
    }
}
