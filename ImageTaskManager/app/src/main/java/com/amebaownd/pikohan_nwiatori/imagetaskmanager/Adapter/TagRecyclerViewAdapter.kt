package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.Tags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R

class TagRecyclerViewAdapter(context: Context, private val tags:List<Tags>?) : RecyclerView.Adapter<TagRecyclerViewAdapter.TagListViewHolder>(){
    private val inflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewTyape: Int): TagListViewHolder {
        val view = inflater.inflate(R.layout.tag_row,parent,false)
        return TagListViewHolder(view)
    }

    override fun getItemCount()=if(tags!=null) tags.size else 0

    override fun onBindViewHolder(viewHolder: TagListViewHolder, position: Int) {
        viewHolder.name.text = Editable.Factory.getInstance().newEditable(tags!![position].name)
        viewHolder.name.isEnabled = false
        viewHolder.editImageButton.setOnClickListener{
            viewHolder.name.background = null
            viewHolder.name.isEnabled=true
            viewHolder.name.isFocusable=true
        }
        viewHolder.deleteEditText.setOnClickListener{

        }

    }

    class TagListViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val name = view.findViewById<EditText>(R.id.tag_row_editText)!!
        val editImageButton = view.findViewById<ImageButton>(R.id.tag_row_edit_imageButton)!!
        val deleteEditText=view.findViewById<ImageButton>(R.id.tag_row_delete_imageButton)!!
    }
}