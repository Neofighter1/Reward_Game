package com.sharpbytes.rewards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.sharpbytes.rewards.databinding.FragmentWalletBinding
import com.sharpbytes.rewards.data.WalletRepository
import com.sharpbytes.rewards.viewmodel.AuthViewModel

class WalletFragment : Fragment() {
    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val walletRepository = WalletRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.balanceText.text = "Available Points: ${user.totalPoints}"
                loadTransactionHistory(user.uid)
            }
        }

        binding.redeemButton.setOnClickListener {
            Snackbar.make(binding.root, "Redemption coming soon!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun loadTransactionHistory(uid: String) {
        // Load transaction history
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
