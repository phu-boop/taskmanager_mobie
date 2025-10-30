package com.example.taskmanager.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.model.Priority
import com.example.taskmanager.model.Task
import com.example.taskmanager.model.TaskStatus
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onMarkDone: (Task) -> Unit,
    private val onMarkInProgress: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvDueDate: TextView = itemView.findViewById(R.id.tvDueDate)
        val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val btnDone: Button = itemView.findViewById(R.id.btnDone)
        val btnInProgress: Button = itemView.findViewById(R.id.btnInProgress)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val priorityIndicator: View = itemView.findViewById(R.id.priorityIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_improved, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)

        holder.tvTitle.text = task.title
        holder.tvDescription.text = task.description
        holder.tvStatus.text = task.status.name
        holder.tvCategory.text = task.category
        holder.tvPriority.text = task.priority.name

        // Set due date
        task.dueDate?.let {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            holder.tvDueDate.text = dateFormat.format(Date(it))
        } ?: run {
            holder.tvDueDate.text = "No due date"
        }

        // Set status color
        when (task.status) {
            TaskStatus.COMPLETED -> {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.completed))
                holder.btnDone.visibility = View.GONE
                holder.btnInProgress.visibility = View.VISIBLE
            }
            TaskStatus.IN_PROGRESS -> {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.in_progress))
                holder.btnDone.visibility = View.VISIBLE
                holder.btnInProgress.visibility = View.GONE
            }
            TaskStatus.PENDING -> {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.pending))
                holder.btnDone.visibility = View.VISIBLE
                holder.btnInProgress.visibility = View.VISIBLE
            }
        }

        // Set priority color
        when (task.priority) {
            Priority.HIGH -> holder.priorityIndicator.setBackgroundColor(Color.RED)
            Priority.MEDIUM -> holder.priorityIndicator.setBackgroundColor(Color.YELLOW)
            Priority.LOW -> holder.priorityIndicator.setBackgroundColor(Color.GREEN)
        }

        // Set overdue style
        if (task.isOverdue()) {
            holder.tvDueDate.setTextColor(Color.RED)
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFE0E0"))
        }

        // Click listeners
        holder.itemView.setOnClickListener {
            onTaskClick(task)
        }

        holder.btnDone.setOnClickListener {
            onMarkDone(task)
        }

        holder.btnInProgress.setOnClickListener {
            onMarkInProgress(task)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(task)
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}