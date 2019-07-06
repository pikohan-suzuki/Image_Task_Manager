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
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.MemoRecyclerViewAdapter
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R

class MemoListFragment(): Fragment(){

    lateinit var recyclerView:RecyclerView
    lateinit var db :AppDatabase
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_memo_list,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Room.databaseBuilder(view.context, AppDatabase::class.java, "database").build()
        setViews()
    }

    private fun setViews(){
        recyclerView = view!!.findViewById(R.id.memo_list_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(view!!.context,LinearLayoutManager.VERTICAL,false)
        setAdapter()
    }

    private fun setAdapter(){
        db.memosDao().loadMemosAndMemoTags().observe(viewLifecycleOwner, Observer {
            val adapter = MemoRecyclerViewAdapter(view!!.context,it)
            recyclerView.adapter=adapter
        })

    }
}
