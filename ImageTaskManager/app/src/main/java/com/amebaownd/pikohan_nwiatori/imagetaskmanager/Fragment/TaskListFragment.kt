package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R

class TaskListFragment(): Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_task_list,container,false)
        return view
    }
}