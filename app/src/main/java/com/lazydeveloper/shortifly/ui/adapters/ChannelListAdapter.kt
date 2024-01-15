package com.lazydeveloper.shortifly.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lazydeveloper.shortifly.data.models.Channel
import com.lazydeveloper.shortifly.data.models.VideoResult
import com.lazydeveloper.shortifly.databinding.SingChannelItemBinding
import com.lazydeveloper.shortifly.ui.fragments.SubscribeFragment
import com.lazydeveloper.shortifly.utils.extensions.diffCallback
import com.lazydeveloper.shortifly.utils.extensions.onClick

class ChannelListAdapter(private val itemClickListener: SubscribeFragment) :
    ListAdapter<Channel, ChannelListAdapter.PostViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK =
            diffCallback<Channel>(
                areItemsTheSame = {oldItem, newItem -> oldItem == newItem },
                areContentsTheSame = {oldItem, newItem -> oldItem == newItem }
            )
    }

    class PostViewHolder private constructor(
        private val binding: SingChannelItemBinding,
        private val itemClickListener: SubscribeFragment
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Channel) {
            binding.data = item
            Glide.with(binding.root.context)
                .load(item.image)
                .into(binding.imgProfile)

            binding.root onClick {
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: SubscribeFragment
            ): PostViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SingChannelItemBinding.inflate(layoutInflater, parent, false)
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