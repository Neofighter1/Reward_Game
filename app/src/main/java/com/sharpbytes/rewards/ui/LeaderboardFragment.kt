package com.sharpbytes.rewards.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharpbytes.rewards.databinding.FragmentLeaderboardBinding
import com.sharpbytes.rewards.viewmodel.LeaderboardViewModel

class LeaderboardFragment : Fragment() {
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private val leaderboardViewModel: LeaderboardViewModel by activityViewModels()
    private lateinit var adapter: LeaderboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LeaderboardAdapter()
        binding.leaderboardRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.leaderboardRecyclerView.adapter = adapter

        leaderboardViewModel.leaderboard.observe(viewLifecycleOwner) { entries ->
            adapter.submitList(entries)
        }

        leaderboardViewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LeaderboardViewModel.LeaderboardState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is LeaderboardViewModel.LeaderboardState.Success -> {
                    binding.progressBar.visibility = View.GONE
                }
                is LeaderboardViewModel.LeaderboardState.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                else -> {}
            }
        }

        leaderboardViewModel.loadLeaderboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class LeaderboardAdapter : androidx.recyclerview.widget.ListAdapter<com.sharpbytes.rewards.data.LeaderboardEntry, LeaderboardAdapter.ViewHolder>(
    LeaderboardDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = android.widget.LinearLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            orientation = android.widget.LinearLayout.HORIZONTAL
            addView(android.widget.TextView(parent.context).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f)
                tag = "rank"
            })
            addView(android.widget.TextView(parent.context).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f)
                tag = "name"
            })
            addView(android.widget.TextView(parent.context).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f)
                tag = "points"
            })
        }
        return ViewHolder(view as android.widget.LinearLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        holder.bind(entry)
    }

    class ViewHolder(private val container: android.widget.LinearLayout) : androidx.recyclerview.widget.RecyclerView.ViewHolder(container) {
        fun bind(entry: com.sharpbytes.rewards.data.LeaderboardEntry) {
            (container.findViewWithTag<android.widget.TextView>("rank")).text = entry.rank.toString()
            (container.findViewWithTag<android.widget.TextView>("name")).text = entry.displayName
            (container.findViewWithTag<android.widget.TextView>("points")).text = entry.totalPoints.toString()
        }
    }
}

class LeaderboardDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<com.sharpbytes.rewards.data.LeaderboardEntry>() {
    override fun areItemsTheSame(oldItem: com.sharpbytes.rewards.data.LeaderboardEntry, newItem: com.sharpbytes.rewards.data.LeaderboardEntry): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: com.sharpbytes.rewards.data.LeaderboardEntry, newItem: com.sharpbytes.rewards.data.LeaderboardEntry): Boolean {
        return oldItem == newItem
    }
}
