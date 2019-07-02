package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Fragment

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.Tags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import kotlinx.android.synthetic.*

class AddMemoFragment(): Fragment(){

    lateinit var createdTextView :TextView
    lateinit var db: AppDatabase
    lateinit var searchEditText:EditText
    lateinit var listView:ListView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_memo,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Room.databaseBuilder(view.context, AppDatabase::class.java, "database").build()
        listView = view.findViewById(R.id.add_memo_listView)
        val time = System.currentTimeMillis()
        createdTextView=view.findViewById(R.id.add_memo_created_textView)
        createdTextView.text = getString(R.string.add_memo_created,SimpleDateFormat("MM").format(time).toInt(),SimpleDateFormat("dd").format(time).toInt(),SimpleDateFormat("HH").format(time).toInt(),SimpleDateFormat("mm").format(time).toInt())
        searchEditText=view.findViewById(R.id.add_memo_search_tags_editText)
        searchEditText.addTextChangedListener(object :TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                db.tagsDao().getTagsByKey("%"+searchEditText.text.toString()+"%").observe(viewLifecycleOwner , Observer<List<Tags>> {
                    val nameList = mutableListOf<String>()
                    var adapter =ArrayAdapter(view.context, android.R.layout.simple_list_item_1, arrayOf(""))
                    if(it!=null) {
                        for (i in 0 until it.size) {
                            nameList.add(it[i].name)
                        }
                        adapter = ArrayAdapter(view.context, android.R.layout.simple_list_item_1, nameList)

                    }
                    listView.adapter=adapter
                })
            }
        })


    }
}
