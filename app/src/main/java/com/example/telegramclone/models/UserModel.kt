package com.example.telegramclone.models

data class UserModel(
    var id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullName: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty"
)
