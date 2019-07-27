package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Fragment

import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.GetAllTag
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.TaskRecyclerViewAdapter
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.Tags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.TasksAndTaskTags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import com.google.android.flexbox.FlexboxLayout

class TaskListFragment(): Fragment(),GetAllTag{
    override fun getAllTag(taskAndTags: TasksAndTaskTags, flexboxLayout: FlexboxLayout){
        db.tagsDao().getAllTags().observe(viewLifecycleOwner, Observer {tagAllList->
            taskAndTags.taskTags.forEach{taskTags->
                val tagName = tagAllList!!.filter { it.tagId == taskTags.tagId }[0].name
                val tagView = inflater.inflate(R.layout.tag,flexboxLayout, false)
                tagView.findViewById<TextView>(R.id.tag_textView).text = tagName
                flexboxLayout.addView(tagView)
            }

        })
    }


    lateinit var db:AppDatabase
    lateinit var inflater:LayoutInflater
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_list,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = Room.databaseBuilder(view.context, AppDatabase::class.java, "database").build()
        inflater= LayoutInflater.from(view.context)
        val recyclerView = view.findViewById<RecyclerView>(R.id.task_list_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(view.context,LinearLayoutManager.VERTICAL,false)
        db.tasksDao().loadTaskAndTaskTagsSortByCreated().observe(viewLifecycleOwner, Observer {
            val adapter  = TaskRecyclerViewAdapter(view.context,it!!)
            adapter.setListener(this)
            recyclerView.adapter=adapter
        })

    }
}