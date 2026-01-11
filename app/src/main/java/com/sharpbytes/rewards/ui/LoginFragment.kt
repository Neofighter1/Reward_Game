package com.sharpbytes.rewards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sharpbytes.rewards.databinding.FragmentLoginBinding
import com.sharpbytes.rewards.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.login(email, password)
            } else {
                Snackbar.make(binding.root, "Please enter email and password", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.signupLink.setOnClickListener {
            findNavController().navigate(android.R.id.home)
        }

        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.loginButton.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    findNavController().navigate(android.R.id.home)
                }
                is AuthViewModel.AuthState.Error -> {
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                    binding.loginButton.isEnabled = true
                }
                else -> {
                    binding.loginButton.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
