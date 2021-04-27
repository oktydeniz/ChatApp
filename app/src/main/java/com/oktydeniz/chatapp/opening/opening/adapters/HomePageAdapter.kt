package com.oktydeniz.chatapp.opening.opening.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.oktydeniz.chatapp.databinding.HomePageItemBinding
import com.oktydeniz.chatapp.opening.opening.models.UserProfile
import com.oktydeniz.chatapp.opening.opening.utils.RecyclerViewClickInterface
import com.oktydeniz.chatapp.opening.opening.views.home.HomeFragmentDirections

class HomePageAdapter(
    private val list: ArrayList<UserProfile>,
    private val clickInterface: RecyclerViewClickInterface
) :
    RecyclerView.Adapter<HomePageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HomePageItemBinding.inflate(inflater)
        return ViewHolder(binding, clickInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder constructor(
        private val binding: HomePageItemBinding,
        private val clickInterface: RecyclerViewClickInterface
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserProfile) {
            binding.user = item
            binding.executePendingBindings()
            binding.userFrame.setOnClickListener {
                clickInterface.onItemClick(item.userId)
            }
        }
    }
}