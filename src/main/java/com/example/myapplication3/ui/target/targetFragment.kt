package com.example.myapplication3.ui.target

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication3.R
import java.util.*

class TargetFragment : Fragment() {

    private lateinit var taskInput: EditText
    private lateinit var addTaskButton: View
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private val tasks: MutableList<Task> = mutableListOf()  // 可变列表，用于存储任务

    data class Task(val description: String, val isChecked: Boolean)

    private lateinit var taskAdapter: TaskAdapter  // RecyclerView 的适配器

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_target, container, false)

        taskInput = view.findViewById(R.id.task_input)
        addTaskButton = view.findViewById(R.id.add_task_button)
        taskRecyclerView = view.findViewById(R.id.task_list)
        sharedPreferences = requireActivity().getSharedPreferences("task_data", Context.MODE_PRIVATE)

        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskAdapter = TaskAdapter(tasks)
        taskRecyclerView.adapter = taskAdapter

        // 加载任务
        loadTasks()

        // 添加任务
        addTaskButton.setOnClickListener {
            val taskText = taskInput.text.toString()

            if (taskText.isNotEmpty()) {
                val newTask = Task(taskText, false)  // 新任务，默认未选中
                tasks.add(newTask)
                taskAdapter.notifyItemInserted(tasks.size - 1)  // 通知适配器
                taskInput.text.clear()  // 清空输入框
                saveTasks()  // 保存任务
            } else {
                Toast.makeText(requireContext(), "请输入任务", Toast.LENGTH_SHORT).show()
            }
        }

        // 使用 ItemTouchHelper 进行拖动排序
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                // 启用上下拖动
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                // 获取源位置和目标位置
                val sourcePos = source.adapterPosition
                val targetPos = target.adapterPosition
                // 交换任务位置
                Collections.swap(tasks, sourcePos, targetPos)
                taskAdapter.notifyItemMoved(sourcePos, targetPos)  // 通知适配器
                saveTasks()  // 保存更新后的任务顺序
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 不需要滑动操作
            }
        })

        itemTouchHelper.attachToRecyclerView(taskRecyclerView)  // 绑定到 RecyclerView

        return view
    }

    // RecyclerView 的适配器
    inner class TaskAdapter(private val tasks: MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task = tasks[position]
            holder.bind(task)
        }

        override fun getItemCount(): Int = tasks.size

        inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val taskDescription: TextView = itemView.findViewById(R.id.task_description)
            private val checkBox: CheckBox = itemView.findViewById(R.id.task_checkbox)

            fun bind(task: Task) {
                taskDescription.text = task.description
                checkBox.isChecked = task.isChecked

                // 任务点击删除
                itemView.setOnClickListener {
                    // 弹出删除确认对话框
                    AlertDialog.Builder(requireContext())
                        .setTitle("确认删除")
                        .setMessage("确定要删除这个任务吗？")
                        .setPositiveButton("确认") { _, _ ->
                            val pos = adapterPosition
                            tasks.removeAt(pos)  // 从任务列表中移除
                            notifyItemRemoved(pos)  // 通知适配器更新视图
                            saveTasks()  // 保存更新后的任务列表
                        }
                        .setNegativeButton("取消", null)
                        .show()
                }

                // 复选框状态改变时
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    tasks[adapterPosition] = Task(task.description, isChecked)  // 更新任务状态
                    saveTasks()  // 保存更新后的任务状态
                }
            }
        }
    }

    private fun loadTasks() {
        tasks.clear() // 清空当前任务列表
        var index = 0
        while (true) {
            // 从SharedPreferences按索引读取任务
            val taskStr = sharedPreferences.getString("task_$index", null) ?: break
            val parts = taskStr.split("|")
            if (parts.size == 2) {
                val taskText = parts[0]
                val isChecked = parts[1].toBoolean()
                tasks.add(Task(taskText, isChecked))
            }
            index++
        }

        // 如果没有找到新格式的任务，尝试加载旧格式并迁移
        if (index == 0) {
            val savedTasks = sharedPreferences.getStringSet("tasks", null)
            savedTasks?.forEach { taskEntry ->
                val parts = taskEntry.split("|")
                if (parts.size == 2) {
                    val taskText = parts[0]
                    val isChecked = parts[1].toBoolean()
                    tasks.add(Task(taskText, isChecked))
                }
            }
            // 迁移旧数据到新格式并删除旧数据
            if (savedTasks != null) {
                saveTasks()
                sharedPreferences.edit().remove("tasks").apply()
            }
        }

        taskAdapter.notifyDataSetChanged()
    }

    private fun saveTasks() {
        sharedPreferences.edit().apply {
            // 删除所有旧任务条目（包括旧格式）
            val existingKeys = sharedPreferences.all.keys.filter { it.startsWith("task_") }
            existingKeys.forEach { remove(it) }

            // 按索引保存每个任务
            tasks.forEachIndexed { index, task ->
                val key = "task_$index"
                val value = "${task.description}|${task.isChecked}"
                putString(key, value)
            }
            apply()
        }
    }
}
