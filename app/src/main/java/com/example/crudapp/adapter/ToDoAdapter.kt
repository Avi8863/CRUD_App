package com.example.crudapp.adapter

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crudapp.AddNewTask
import com.example.crudapp.MainActivity
import com.example.crudapp.R
import com.example.crudapp.model.ToDoModel
import com.google.firebase.firestore.FirebaseFirestore

class ToDoAdapter(mainActivity: MainActivity, todoList: MutableList<ToDoModel>) :
    RecyclerView.Adapter<ToDoAdapter.MyViewHolder>() {
    private val todoList: MutableList<ToDoModel>
    private val activity: MainActivity
    private var firestore: FirebaseFirestore? = null

    init {
        this.todoList = todoList
        activity = mainActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.each_task, parent, false)
        firestore = FirebaseFirestore.getInstance()
        return MyViewHolder(view)
    }

    fun deleteTask(position: Int) {
        val toDoModel: ToDoModel = todoList[position]
        toDoModel.TaskId?.let { firestore!!.collection("task").document(it).delete() }
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }

    val context: Context
        get() = activity

    fun editTask(position: Int) {
        val toDoModel: ToDoModel = todoList[position]
        val bundle = Bundle()
        var todoModel = ToDoModel()
        bundle.putString("task", todoModel.task)
        bundle.putString("due", todoModel.due)
        bundle.putString("id", todoModel.TaskId)
        val addNewTask = AddNewTask()
        addNewTask.setArguments(bundle)
        addNewTask.show(activity.getSupportFragmentManager(), addNewTask.getTag())
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val toDoModel: ToDoModel = todoList[position]
        holder.mCheckBox.setText(toDoModel.task)
        holder.mDueDateTv.text = "Due On " + toDoModel.due
        holder.mCheckBox.isChecked = toBoolean(toDoModel.status)
        holder.ivBold.setOnClickListener {
            holder.mCheckBox.setTypeface(null, Typeface.BOLD)
            holder.mDueDateTv.setTypeface(null, Typeface.BOLD)
            holder.mCheckBox.setPaintFlags(holder.mCheckBox.getPaintFlags() and Paint.UNDERLINE_TEXT_FLAG.inv())
            holder.mDueDateTv.setPaintFlags(holder.mDueDateTv.getPaintFlags() and Paint.UNDERLINE_TEXT_FLAG.inv())
        }
        holder.ivItalic.setOnClickListener {
            holder.mCheckBox.setTypeface(null, Typeface.ITALIC)
            holder.mDueDateTv.setTypeface(null, Typeface.ITALIC)
            holder.mCheckBox.setPaintFlags(holder.mCheckBox.getPaintFlags() and Paint.UNDERLINE_TEXT_FLAG.inv())
            holder.mDueDateTv.setPaintFlags(holder.mDueDateTv.getPaintFlags() and Paint.UNDERLINE_TEXT_FLAG.inv())
        }
        holder.ivUnderline.setOnClickListener {
            holder.mCheckBox.paintFlags = holder.mCheckBox.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            holder.mDueDateTv.paintFlags = holder.mDueDateTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
        holder.mCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                toDoModel.TaskId?.let {
                    firestore!!.collection("task").document(it).update("status", 1)
                }
                holder.mCheckBox.setPaintFlags(holder.mCheckBox.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                holder.mDueDateTv.setPaintFlags(holder.mDueDateTv.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                holder.mCheckBox.setPaintFlags(holder.mCheckBox.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
                holder.mDueDateTv.setPaintFlags(holder.mDueDateTv.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
                toDoModel.TaskId?.let {
                    firestore!!.collection("task").document(it).update("status", 0)
                }
            }
        }
    }

    private fun toBoolean(status: Int): Boolean {
        return status != 0
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mDueDateTv: TextView
        var mCheckBox: CheckBox
        var ivBold: ImageView
        var ivUnderline: ImageView
        var ivItalic: ImageView

        init {
            mDueDateTv = itemView.findViewById<TextView>(R.id.due_date_tv)
            mCheckBox = itemView.findViewById<CheckBox>(R.id.mcheckbox)
            ivBold = itemView.findViewById<ImageView>(R.id.ivBold)
            ivUnderline = itemView.findViewById<ImageView>(R.id.ivUnderline)
            ivItalic = itemView.findViewById<ImageView>(R.id.ivItalic)
        }
    }
}