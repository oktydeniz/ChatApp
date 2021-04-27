package com.oktydeniz.chatapp.opening.opening.utils

interface RecyclerViewClickInterface {
    fun onItemClick(position: Int)
    fun onItemClick(userId: String)
    fun onItemClickYes(userId: String, otherUserId: String)
    fun onItemClickNo(userId: String, otherUserId: String)
}