package com.parrosz.storyu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.parrosz.storyu.adapter.StoriesAdapter
import com.parrosz.storyu.adapter.StoryLoadingStateAdapter
import com.parrosz.storyu.databinding.FragmentStoryListBinding
import com.parrosz.storyu.StoryListViewModel.*
import kotlinx.coroutines.launch


class StoryListFragment : Fragment() {

    private var _binding: FragmentStoryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: StoryListViewModel by viewModels {
            StoryListViewModel.StoryListViewModelFactory.getInstance(requireContext())
        }
        setupAdapter()
        setupData(viewModel)
    }

    private fun setupAdapter() {
        adapter = StoriesAdapter(requireActivity())

        binding.rvStories.apply {
            adapter = this@StoryListFragment.adapter.withLoadStateFooter(
                footer = StoryLoadingStateAdapter {
                    retry()
                }
            )

            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
        }

        binding.swipeLayout.setOnRefreshListener {
            binding.tvError.visibility = View.INVISIBLE
            adapter.retry()
            adapter.refresh()
        }

        adapter.addLoadStateListener { loadState ->
            if (loadState.mediator?.refresh is LoadState.Loading) {
                if (adapter.snapshot().isEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                }

                binding.tvNoStory.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.swipeLayout.isRefreshing = false

                val error = when {
                    loadState.mediator?.refresh is LoadState.Error -> loadState.mediator?.refresh as LoadState.Error
                    loadState.mediator?.prepend is LoadState.Error -> loadState.mediator?.prepend as LoadState.Error
                    loadState.mediator?.append is LoadState.Error -> loadState.mediator?.append as LoadState.Error
                    else -> null
                }

                error?.let {
                    if (adapter.snapshot().isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.failed_fetching_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun retry() {
        adapter.retry()
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun setupData(viewModel: StoryListViewModel) {
        viewModel.getToken().observe(requireActivity()) {
            if (it != "null") {
                getData(viewModel, "Bearer $it")
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getData(viewModel: StoryListViewModel, token: String) {
        lifecycleScope.launch {
            viewModel.getStories(token).observe(requireActivity()) {
                adapter.submitData(requireActivity().lifecycle, it)
            }
        }
    }
}