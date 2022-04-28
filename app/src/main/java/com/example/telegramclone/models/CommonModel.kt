package com.example.telegramclone.models

import java.io.Serializable

data class CommonModel(
    var id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullName: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty",

    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timeStamp: Any = "",
    var fileUrl: String = "empty",

    var lastMessage: String = "",
    var choice: Boolean = false

) : Serializable {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}