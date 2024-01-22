package com.lazydeveloper.shortifly.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lazydeveloper.shortifly.data.models.VideoResult
import com.lazydeveloper.shortifly.databinding.SingleSearchItemBinding
import com.lazydeveloper.shortifly.ui.fragments.HomeFragment
import com.lazydeveloper.shortifly.utils.extensions.diffCallback
import com.lazydeveloper.shortifly.utils.extensions.onClick

class SearchListAdapter(private val itemClickListener: ItemClickListener) :
    ListAdapter<VideoResult, SearchListAdapter.PostViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK =
            diffCallback<VideoResult>(
                areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
                areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
            )
    }

    inner class PostViewHolder(private val binding: SingleSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoResult) {
            binding.data = item
            Glide.with(binding.root.context)
                .load(item.thumbnail)
                .into(binding.imgThumbnail)

            binding.root onClick  {
                itemClickListener.onItemClickListener(item)
            }
            binding.imgMore onClick {
                itemClickListener.onMoreClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = SingleSearchItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    interface ItemClickListener {
        fun onItemClickListener(item: VideoResult)
        fun onMoreClickListener(item: VideoResult)
    }
}