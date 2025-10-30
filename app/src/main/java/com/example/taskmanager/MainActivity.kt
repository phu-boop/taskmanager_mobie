package com.example.taskmanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.taskmanager.adapter.TaskAdapter
import com.example.taskmanager.databinding.ActivityMainBinding
import com.example.taskmanager.model.Task
import com.example.taskmanager.model.TaskStatus
import com.example.taskmanager.viewmodel.TaskViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel = TaskViewModel()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
    }

    private fun updateUI(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            binding.rvTasks.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.rvTasks.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onTaskClick = { task ->
                showEditTaskDialog(task)
            },
            onMarkDone = { task ->
                viewModel.markTaskAsDone(task)
            },
            onMarkInProgress = { task ->
                viewModel.markTaskAsInProgress(task)
            },
            onDelete = { task ->
                showDeleteConfirmation(task)
            }
        )

        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.filteredTasks.collectLatest { tasks ->
                adapter.submitList(tasks)
                updateUI(tasks)
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFilter = when (position) {
                    0 -> null
                    1 -> TaskStatus.PENDING
                    2 -> TaskStatus.IN_PROGRESS
                    3 -> TaskStatus.COMPLETED
                    else -> null
                }
                viewModel.setFilter(selectedFilter)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                viewModel.setFilter(null)
            }
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add New Task")
            .create()

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isNotEmpty()) {
                val newTask = Task(title = title, description = description)
                viewModel.addTask(newTask)
                dialog.dismiss()
            } else {
                etTitle.error = "Title is required"
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditTaskDialog(task: Task) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        etTitle.setText(task.title)
        etDescription.setText(task.description)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Edit Task")
            .create()

        btnSave.setOnClickListener {
            val newTitle = etTitle.text.toString().trim()
            val newDescription = etDescription.text.toString().trim()

            if (newTitle.isNotEmpty()) {
                val updatedTask = task.copy(title = newTitle, description = newDescription)
                viewModel.updateTask(updatedTask)
                dialog.dismiss()
            } else {
                etTitle.error = "Title is required"
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete '${task.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}