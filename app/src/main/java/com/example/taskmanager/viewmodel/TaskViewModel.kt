package com.example.taskmanager.viewmodel


import androidx.lifecycle.ViewModel
import com.example.taskmanager.model.Task

class TaskViewModel : ViewModel() {
    private val _taskList = mutableListOf<Task>()
    val taskList: List<Task> get() = _taskList

    fun addTask(task: Task) {
        _taskList.add(task.copy(id = _taskList.size + 1))
    }

    fun markTaskAsDone(taskIndex: Int) {
        if (taskIndex in _taskList.indices) {
            _taskList[taskIndex] = _taskList[taskIndex].copy(status = "done")
        }
    }

    fun deleteTask(taskIndex: Int) {
        if (taskIndex in _taskList.indices) {
            _taskList.removeAt(taskIndex)
        }
    }

    fun getTaskCount(): Int = _taskList.size
}