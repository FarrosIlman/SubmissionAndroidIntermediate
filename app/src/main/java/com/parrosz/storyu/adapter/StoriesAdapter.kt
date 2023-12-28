package com.parrosz.storyu.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.parrosz.storyu.data.remote.responses.StoryEntity
import com.parrosz.storyu.databinding.ItemPostBinding
import com.parrosz.storyu.detail.DetailActivity

class StoriesAdapter(private val context: Context) :
    PagingDataAdapter<StoryEntity, StoriesAdapter.ViewHolder>(StoryDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)

        holder.binding.cardStory.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.STORY_EXTRA, story)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context as Activity,
                    Pair(holder.binding.imgPost, "picture"),
                    Pair(holder.binding.tvCaption, "description")
                )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    inner class ViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity?) {
            binding.let {
                binding.tvUserName.text = story?.name
                binding.tvCaption.text = story?.description

                Glide.with(itemView.context)
                    .load(story?.photoUrl)
                    .into(binding.imgPost)
            }
        }
    }

    object StoryDiffCallback : DiffUtil.ItemCallback<StoryEntity>() {
        override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem == newItem
        }

    }
}