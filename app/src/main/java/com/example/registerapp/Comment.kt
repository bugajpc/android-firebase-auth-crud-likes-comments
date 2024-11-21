package com.example.registerapp

import com.google.firebase.Timestamp

data class Comment(val id: String, val uid: String, val pid: String, val content: String, val timestamp: Timestamp)
