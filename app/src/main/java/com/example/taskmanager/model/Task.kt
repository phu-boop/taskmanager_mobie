package com.example.taskmanager.model

data class Task(
    val title: String,
    val description: String,
    var status: String = "not done",
    val id: Int = 0
)