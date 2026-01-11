package com.sharpbytes.rewards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sharpbytes.rewards.databinding.FragmentDashboardBinding
import com.sharpbytes.rewards.viewmodel.AuthViewModel
import com.sharpbytes.rewards.viewmodel.StreakViewModel

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val streakViewModel: StreakViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.userNameText.text = user.displayName
                binding.totalPointsText.text = "Points: ${user.totalPoints}"
                binding.streakText.text = "Streak: ${user.streakDays} days"
            }
        }

        streakViewModel.streakDays.observe(viewLifecycleOwner) { days ->
            binding.streakText.text = "Streak: $days days"
        }

        binding.playGameButton.setOnClickListener {
            // Navigate to game fragment
        }

        binding.skillStreakButton.setOnClickListener {
            // Navigate to skill streak fragment
        }

        binding.walletButton.setOnClickListener {
            // Navigate to wallet fragment
        }

        binding.leaderboardButton.setOnClickListener {
            // Navigate to leaderboard fragment
        }

        binding.ideaButton.setOnClickListener {
            // Navigate to idea submission fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
