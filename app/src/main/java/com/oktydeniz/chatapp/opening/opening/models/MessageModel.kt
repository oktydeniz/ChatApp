package com.oktydeniz.chatapp.opening.opening.models

data class MessageModel(

    val currentUserId: String,
    val userId: String,
    val type: String,
    val time: String,
    val seen: Boolean?,
    val text: String,
    val from: String
) {
    constructor() : this("", "", "", "", null, "", "")
}