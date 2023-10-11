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

class SearchListAdapter(private val itemClickListener: HomeFragment) :
    ListAdapter<VideoResult, SearchListAdapter.PostViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK =
            diffCallback<VideoResult>(
                areItemsTheSame = {oldItem, newItem -> oldItem == newItem },
                areContentsTheSame = {oldItem, newItem -> oldItem == newItem }
            )
    }

    class PostViewHolder private constructor(
        private val binding: SingleSearchItemBinding,
        private val itemClickListener: HomeFragment
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoResult) {
            binding.data = item
            Glide.with(binding.root.context)
                .load(item.thumbnail)
                .into(binding.imgThumbnail)

            binding.root onClick {
                itemClickListener?.onItemClickListener(item)
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: HomeFragment
            ): PostViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SingleSearchItemBinding.inflate(layoutInflater, parent, false)
                return PostViewHolder(binding, itemClickListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder.from(parent, itemClickListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    interface ItemClickListener {
        fun onItemClickListener(item: VideoResult)
    }
}