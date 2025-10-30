package com.example.taskmanager.repository

import com.example.taskmanager.database.TaskDao
import com.example.taskmanager.database.TaskEntity
import com.example.taskmanager.model.Priority
import com.example.taskmanager.model.Task
import com.example.taskmanager.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toTask() }
        }
    }

    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> {
        return taskDao.getTasksByStatus(status).map { entities ->
            entities.map { it.toTask() }
        }
    }

    suspend fun addTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    suspend fun markTaskAsDone(taskId: Int) {
        // Implementation would get task and update status
    }

    private fun Task.toEntity(): TaskEntity {
        return TaskEntity(
            id = this.id,
            title = this.title,
            description = this.description,
            status = this.status,
            createdAt = this.createdAt,
            dueDate = this.dueDate,
            priority = this.priority,
            category = this.category
        )
    }

    private fun TaskEntity.toTask(): Task {
        return Task(
            id = this.id,
            title = this.title,
            description = this.description,
            status = this.status,
            createdAt = this.createdAt,
            dueDate = this.dueDate,
            priority = this.priority,
            category = this.category
        )
    }
}