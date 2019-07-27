package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity.MemoInfoActivity
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity.TaskInfoActivity
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.Tags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.TasksAndTaskTags

import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import com.google.android.flexbox.FlexboxLayout


interface GetAllTag {
    fun getAllTag(taskAndTags:TasksAndTaskTags,flexboxLayout: FlexboxLayout)
}

class TaskRecyclerViewAdapter(private val context: Context, private val taskAndTaskTags: List<TasksAndTaskTags>) :
    RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskRecyclerViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var listener: GetAllTag? = null
    lateinit var view: View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskRecyclerViewHolder {
       view = inflater.inflate(R.layout.task_card, parent, false)
        return TaskRecyclerViewHolder(view)
    }

    override fun getItemCount() = taskAndTaskTags.size

    override fun onBindViewHolder(viewHolder: TaskRecyclerViewHolder, position: Int) {
        viewHolder.title.text = context.getString(R.string.task_card_title, taskAndTaskTags[position].task.title)
        viewHolder.deadline.text = context.getString(R.string.task_card_deadline, 1, 1, 1, 1)
        if (listener != null) {
            listener!!.getAllTag(taskAndTaskTags[position],viewHolder.tagFlexboxLayout)
        }
        viewHolder.card.setOnClickListener{
            val intent = Intent(view.context, TaskInfoActivity::class.java)
            intent.putExtra("taskId", taskAndTaskTags[position].task.taskId)
            view.context.startActivity(intent)
        }
    }

    class TaskRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card = view.findViewById<CardView>(R.id.task_card_cardView)
        val title = view.findViewById<TextView>(R.id.task_card_title)
        val deadline = view.findViewById<TextView>(R.id.task_card_deadline)
        val tagFlexboxLayout = view.findViewById<FlexboxLayout>(R.id.task_card_flexboxLayout)
    }

    fun setListener(listener: GetAllTag) {
        this.listener = listener
    }

}