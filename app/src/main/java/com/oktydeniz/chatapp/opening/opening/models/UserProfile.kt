package com.oktydeniz.chatapp.opening.opening.models

data class UserProfile(
    var userName: String,
    var userMail: String,
    var userImage: String,
    var userBirthday: String,
    var education: String,
    var aboutUser: String,
    var userId:String
) {
    constructor() : this("", "", "", "", "", "","")
}