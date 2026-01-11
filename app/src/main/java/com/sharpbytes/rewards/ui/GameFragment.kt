package com.sharpbytes.rewards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sharpbytes.rewards.databinding.FragmentGameBinding
import com.sharpbytes.rewards.viewmodel.GameViewModel

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    private var currentQuestionIndex = 0
    private var startTime = 0L
    private var correctAnswers = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startGameButton.setOnClickListener {
            startTime = System.currentTimeMillis()
            gameViewModel.startGame()
        }

        gameViewModel.questions.observe(viewLifecycleOwner) { questions ->
            if (questions.isNotEmpty()) {
                displayQuestion(questions[currentQuestionIndex])
            }
        }

        gameViewModel.gameState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GameViewModel.GameState.Playing -> {
                    binding.startGameButton.visibility = View.GONE
                    binding.questionContainer.visibility = View.VISIBLE
                }
                is GameViewModel.GameState.Finished -> {
                    Snackbar.make(binding.root, "Game finished! Score: ${gameViewModel.score.value}", Snackbar.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is GameViewModel.GameState.Error -> {
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun displayQuestion(question: com.sharpbytes.rewards.data.QuizQuestion) {
        binding.questionText.text = question.question
        binding.optionsContainer.removeAllViews()

        question.options.forEachIndexed { index, option ->
            val button = android.widget.Button(requireContext()).apply {
                text = option
                setOnClickListener {
                    val isCorrect = option == question.correctAnswer
                    if (isCorrect) correctAnswers++
                    gameViewModel.submitAnswer(currentQuestionIndex, option, isCorrect)

                    currentQuestionIndex++
                    if (currentQuestionIndex < (gameViewModel.questions.value?.size ?: 0)) {
                        displayQuestion(gameViewModel.questions.value!![currentQuestionIndex])
                    } else {
                        val timeTaken = (System.currentTimeMillis() - startTime) / 1000
                        // gameViewModel.finishGame(uid, timeTaken, correctAnswers)
                    }
                }
            }
            binding.optionsContainer.addView(button)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
