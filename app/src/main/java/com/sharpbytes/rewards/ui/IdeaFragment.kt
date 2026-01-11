package com.sharpbytes.rewards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.sharpbytes.rewards.databinding.FragmentIdeaBinding
import com.sharpbytes.rewards.data.IdeaRepository
import com.sharpbytes.rewards.data.IdeaSubmission
import com.sharpbytes.rewards.viewmodel.AuthViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IdeaFragment : Fragment() {
    private var _binding: FragmentIdeaBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val ideaRepository = IdeaRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdeaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            val title = binding.titleInput.text.toString().trim()
            val description = binding.descriptionInput.text.toString().trim()
            val category = binding.categoryInput.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty()) {
                submitIdea(title, description, category)
            } else {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitIdea(title: String, description: String, category: String) {
        val user = authViewModel.currentUser.value ?: return
        val idea = IdeaSubmission(
            uid = user.uid,
            title = title,
            description = description,
            category = category,
            points = 50,
            status = "pending"
        )

        GlobalScope.launch {
            val result = ideaRepository.submitIdea(idea)
            result.onSuccess {
                binding.titleInput.text?.clear()
                binding.descriptionInput.text?.clear()
                binding.categoryInput.text?.clear()
                Snackbar.make(binding.root, "Idea submitted successfully!", Snackbar.LENGTH_SHORT).show()
            }
            result.onFailure {
                Snackbar.make(binding.root, "Failed to submit idea", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
