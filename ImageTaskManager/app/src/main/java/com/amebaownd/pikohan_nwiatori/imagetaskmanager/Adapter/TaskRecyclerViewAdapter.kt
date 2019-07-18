package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.Tags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.TasksAndTaskTags

import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import com.google.android.flexbox.FlexboxLayout


interface GetAllTag{
    fun getAllTag():List<Tags>
}

class TaskRecyclerViewAdapter(private val context: Context, private val taskAndTaskTags: List<TasksAndTaskTags>) :
    RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskRecyclerViewHolder>() {

    private val  inflater = LayoutInflater.from(context)
    private var listener :GetAllTag?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskRecyclerViewHolder {
        val view = inflater.inflate(R.layout.task_card,parent,false)
        return TaskRecyclerViewHolder(view)
    }

    override fun getItemCount() = taskAndTaskTags.size

    override fun onBindViewHolder(viewHolder: TaskRecyclerViewHolder, position: Int) {
        viewHolder.title.text=context.getString(R.string.task_card_title,taskAndTaskTags[position].task.title)
        viewHolder.deadline.text=context.getString(R.string.task_card_deadline,1,1,1,1)
        if(listener!=null) {
            val tagAllList=this.listener!!.getAllTag()
            taskAndTaskTags[position].taskTags.forEach { taskTags ->
                val tagName = tagAllList.filter { it.tagId == taskTags.tagId }[0].name
                val tagView = inflater.inflate(R.layout.tag, viewHolder.tagFlexboxLayout, false)
                tagView.findViewById<TextView>(R.id.tag_textView).text = tagName
                viewHolder.tagFlexboxLayout.addView(tagView)
            }
        }
}

    class TaskRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title= view.findViewById<TextView>(R.id.task_card_title)
        val deadline = view.findViewById<TextView>(R.id.task_card_deadline)
        val tagFlexboxLayout = view.findViewById<FlexboxLayout>(R.id.task_card_flexboxLayout)
    }

    fun setListener(listener:GetAllTag){
        this.listener=listener
    }

}