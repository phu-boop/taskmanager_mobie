package com.example.taskmanager

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.adapter.TaskAdapter
import com.example.taskmanager.model.Task
import com.example.taskmanager.viewmodel.TaskViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var rvTasks: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var btnAddTask: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo ViewModel
        viewModel = TaskViewModel()

        // Ánh xạ view
        rvTasks = findViewById(R.id.rvTasks)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnAddTask = findViewById(R.id.btnAddTask)

        // Thiết lập RecyclerView
        adapter = TaskAdapter(
            tasks = viewModel.taskList,
            onMarkDone = { position ->
                viewModel.markTaskAsDone(position)
                updateUI()
            },
            onDelete = { position ->
                showDeleteConfirmation(position)
            }
        )

        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = adapter

        // Sự kiện thêm task
        btnAddTask.setOnClickListener {
            showAddTaskDialog()
        }

        updateUI()
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isNotEmpty()) {
                val newTask = Task(title, description)
                viewModel.addTask(newTask)
                updateUI()
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

    private fun showDeleteConfirmation(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTask(position)
                updateUI()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateUI() {
        adapter.updateTasks(viewModel.taskList)

        if (viewModel.taskList.isEmpty()) {
            rvTasks.visibility = android.view.View.GONE
            tvEmpty.visibility = android.view.View.VISIBLE
        } else {
            rvTasks.visibility = android.view.View.VISIBLE
            tvEmpty.visibility = android.view.View.GONE
        }
    }
}