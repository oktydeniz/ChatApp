package com.oktydeniz.chatapp.opening.opening.models

data class NotificationModel(

    var userName: String,
    var userImage: String,
    var userId: String,
) {
    constructor() : this("", "", "")
}