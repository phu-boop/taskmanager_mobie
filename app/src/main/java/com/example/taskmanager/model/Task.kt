package com.example.taskmanager.model

import java.util.Date

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

data class Task(
    val title: String,
    val description: String,
    var status: TaskStatus = TaskStatus.PENDING,
    val id: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "General"
) {
    fun isOverdue(): Boolean {
        return dueDate != null && dueDate < System.currentTimeMillis() && status != TaskStatus.COMPLETED
    }
}
