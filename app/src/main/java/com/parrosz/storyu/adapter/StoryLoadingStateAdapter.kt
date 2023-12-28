package com.parrosz.storyu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.parrosz.storyu.databinding.LoadingStateBinding


class StoryLoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<StoryLoadingStateAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        if (loadState is LoadState.Loading) {
            holder.binding.progressBar.visibility = View.VISIBLE
            holder.binding.btnRetry.visibility = View.INVISIBLE
            holder.binding.tvErrorMessage.visibility = View.INVISIBLE
        } else {
            holder.binding.progressBar.visibility = View.INVISIBLE
        }

        if (loadState is LoadState.Error) {
            holder.binding.tvErrorMessage.text = loadState.error.localizedMessage
            holder.binding.tvErrorMessage.visibility = View.VISIBLE
            holder.binding.btnRetry.visibility = View.VISIBLE
        }

        holder.binding.btnRetry.setOnClickListener {
            retry.invoke()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return ViewHolder(
            LoadingStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class ViewHolder(val binding: LoadingStateBinding) : RecyclerView.ViewHolder(binding.root)
}