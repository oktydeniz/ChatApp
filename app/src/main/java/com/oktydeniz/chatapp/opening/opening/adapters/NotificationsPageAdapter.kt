package com.oktydeniz.chatapp.opening.opening.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.oktydeniz.chatapp.databinding.NotificationItemRowBinding
import com.oktydeniz.chatapp.opening.opening.models.NotificationModel
import com.oktydeniz.chatapp.opening.opening.utils.RecyclerViewClickInterface
import com.oktydeniz.chatapp.opening.opening.views.home.NotificationFragmentDirections

class NotificationsPageAdapter(
    private val notificationsList: List<NotificationModel>,
    private val clickInterface: RecyclerViewClickInterface
) :
    RecyclerView.Adapter<NotificationsPageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NotificationItemRowBinding.inflate(inflater)
        return ViewHolder(binding, clickInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationsList[position]
        holder.bind(notification)

    }


    override fun getItemCount(): Int {
        return notificationsList.size
    }

    class ViewHolder constructor(
        private val binding: NotificationItemRowBinding,
        private val clickInterface: RecyclerViewClickInterface
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val userId = FirebaseAuth.getInstance().currentUser!!.uid
        fun bind(item: NotificationModel) {
            binding.notification = item

            binding.getUserProfile.setOnClickListener {
                clickInterface.onItemClick(item.userId)
            }
            binding.acceptNotificationRequest.setOnClickListener {
                clickInterface.onItemClickYes(userId, item.userId)

            }
            binding.cancelNotificationRequest.setOnClickListener {
                clickInterface.onItemClickNo(userId, item.userId)
            }
            binding.executePendingBindings()
        }
    }
}