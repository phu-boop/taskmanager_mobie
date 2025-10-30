package com.example.taskmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.model.Task
import com.example.taskmanager.model.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TaskViewModel : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _filter = MutableStateFlow<TaskStatus?>(null)
    val filter: StateFlow<TaskStatus?> = _filter

    fun setFilter(status: TaskStatus?) {
        _filter.value = status
    }
    fun updateTask(updatedTask: Task) {
        val currentTasks = _tasks.value.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            currentTasks[index] = updatedTask
            _tasks.value = currentTasks
        }
    }

    val filteredTasks: StateFlow<List<Task>> = combine(_tasks, _filter) { tasks, filter ->
        if (filter == null) tasks else tasks.filter { it.status == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun addTask(task: Task) {
        val currentTasks = _tasks.value.toMutableList()
        currentTasks.add(task.copy(id = currentTasks.size + 1))
        _tasks.value = currentTasks
    }

    fun markTaskAsDone(task: Task) {
        val currentTasks = _tasks.value.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            currentTasks[index] = currentTasks[index].copy(status = TaskStatus.COMPLETED)
            _tasks.value = currentTasks
        }
    }

    fun deleteTask(task: Task) {
        val currentTasks = _tasks.value.toMutableList()
        currentTasks.removeAll { it.id == task.id }
        _tasks.value = currentTasks
    }

    fun markTaskAsInProgress(task: Task) {
        val currentTasks = _tasks.value.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            currentTasks[index] = currentTasks[index].copy(status = TaskStatus.IN_PROGRESS)
            _tasks.value = currentTasks
        }
    }

}