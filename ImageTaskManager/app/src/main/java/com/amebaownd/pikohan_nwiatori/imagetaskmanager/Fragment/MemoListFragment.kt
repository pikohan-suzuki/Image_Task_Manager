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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.GetTagName
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.MemoRecyclerViewAdapter
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.MemosAndMemoTags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R

interface OnSpinnerItemSelected{
    fun setSortedByCreatedRecyclerViewAdapter()
    fun setSortedByTitleRecyclerViewAdapter()
}

class MemoListFragment(): Fragment(),OnSpinnerItemSelected,GetTagName{
    override fun getTagNameById(tagId: Long,textView: TextView) {
        db.tagsDao().getNameByTagId(tagId).observe(viewLifecycleOwner, Observer {
            textView.text=it
        })
    }

    override fun setSortedByCreatedRecyclerViewAdapter() {
        db.memosDao().loadMemosAndMemoTagsSortByCreated().observe(viewLifecycleOwner, Observer {
            val adapter = MemoRecyclerViewAdapter(view!!.context,it)
            adapter.setListener(this)
            recyclerView.adapter=adapter
        })
    }

    override fun setSortedByTitleRecyclerViewAdapter() {
        db.memosDao().loadMemosAndMemoTagsSortByTitle().observe(viewLifecycleOwner, Observer {
            val adapter = MemoRecyclerViewAdapter(view!!.context,it)
            adapter.setListener(this)
            recyclerView.adapter=adapter
        })
    }

    lateinit var db :AppDatabase
    lateinit var recyclerView:RecyclerView
    lateinit var sortSpinner:Spinner

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
        sortSpinner = view!!.findViewById(R.id.memo_list_sort_spinner)
        setSpinnerAdapter()
        val spinnerSelectedListener = SpinnerSelectedListener()
        spinnerSelectedListener.setListener(this)
        sortSpinner.onItemSelectedListener = spinnerSelectedListener

        setSortedByCreatedRecyclerViewAdapter()
    }

    private fun setSpinnerAdapter(){
        sortSpinner.adapter = ArrayAdapter(view!!.context,android.R.layout.simple_spinner_dropdown_item,arrayOf("作成日時でソート","タイトルでソート"))
    }


    class SpinnerSelectedListener() :AdapterView.OnItemSelectedListener{
        private var listener:OnSpinnerItemSelected?=null
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when(position){
                0->{
                    if(listener!=null)
                        listener!!.setSortedByCreatedRecyclerViewAdapter()
                }
                1->{
                    if(listener!=null)
                        listener!!.setSortedByTitleRecyclerViewAdapter()
                }
            }
        }
        fun setListener(listener:OnSpinnerItemSelected){
            this.listener=listener
        }
    }

}
