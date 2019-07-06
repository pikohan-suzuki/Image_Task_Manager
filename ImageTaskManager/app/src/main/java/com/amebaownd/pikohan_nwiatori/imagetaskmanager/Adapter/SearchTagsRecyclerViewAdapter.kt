package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter

import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.Tags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R

interface onSelectSearchedTags{
    fun onSelectSearchedTags(tagsName:String)
}

class SearchTagsRecyclerViewAdapter(context: Context, private val tags:List<Tags>?) : RecyclerView.Adapter<SearchTagsRecyclerViewAdapter.SearchTagListViewHolder>(){
    private val inflater = LayoutInflater.from(context)
    lateinit var db :AppDatabase
    private var listener :onSelectSearchedTags? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewTyape: Int): SearchTagListViewHolder {
        val view = inflater.inflate(R.layout.search_tags,parent,false)
        db = Room.databaseBuilder(view.context, AppDatabase::class.java, "database").build()
        return SearchTagListViewHolder(view)
    }

    override fun getItemCount()=if(tags!=null) tags.size else 0

    override fun onBindViewHolder(viewHolder: SearchTagListViewHolder, position: Int) {
        if(tags!=null){
            viewHolder.tagText.text = tags[position].name
            viewHolder.tagText.setOnClickListener{
                if(this.listener!=null)
                    listener!!.onSelectSearchedTags(tags[position].name)
            }
        }

    }

    class SearchTagListViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tagText = view.findViewById<TextView>(R.id.search_tag_tag_textView)
    }

    fun setOnSelectSearchedTags(listener:onSelectSearchedTags){
        this.listener = listener
    }
}