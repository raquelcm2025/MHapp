package com.example.myhobbiesapp.firebase.model

data class FriendRequest(
    val fromUid: String = "",
    val toUid: String = "",
    val status: String = "pending",
    val ts: Long = 0L
)
