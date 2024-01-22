package com.lazydeveloper.shortifly.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lazydeveloper.shortifly.R
import com.lazydeveloper.shortifly.data.models.VideoResult
import com.lazydeveloper.shortifly.databinding.SingleSearchItemBinding
import com.lazydeveloper.shortifly.ui.fragments.PlayerFragment
import com.lazydeveloper.shortifly.utils.extensions.diffCallback
import com.lazydeveloper.shortifly.utils.extensions.onClick

class PlayerItemListAdapter :
    ListAdapter<VideoResult, PlayerItemListAdapter.PostViewHolder>(
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
        private val itemClickListener: PlayerFragment
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoResult) {
            binding.data = item

            Log.e("TAG", "bind: $item" )
//                binding.executePendingBindings()

            Glide.with(binding.root.context)
                .load(item.thumbnail)
                .placeholder(R.drawable.loading)
                .into(binding.imgThumbnail)

            binding.root onClick {
                itemClickListener.onItemClickListener(item)
            }
        }

        companion object {
            fun from(
                parent: ViewGroup
            ): PostViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SingleSearchItemBinding.inflate(layoutInflater, parent, false)
                return PostViewHolder(binding, PlayerFragment())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    interface ItemClickListener {
        fun onItemClickListener(item: VideoResult)
    }
}