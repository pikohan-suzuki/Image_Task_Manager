package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Fragment

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.GetAllTag
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.Tags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R

class TaskListFragment(): Fragment(){
    lateinit var db:AppDatabase
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_list,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = Room.databaseBuilder(view.context, AppDatabase::class.java, "database").build()
        val recyclerView = view.findViewById<RecyclerView>(R.id.task_list_recyclerView)

    }
}