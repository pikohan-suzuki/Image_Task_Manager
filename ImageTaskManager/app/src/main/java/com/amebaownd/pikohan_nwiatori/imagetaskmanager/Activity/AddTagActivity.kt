package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity

import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.TagRecyclerViewAdapter
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.Tags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.Tasks
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import kotlin.concurrent.thread

class AddTagActivity : AppCompatActivity() {
    lateinit var db: AppDatabase
    lateinit var addImageButton: ImageButton
    lateinit var addEditText: EditText
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tag)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database").build()

        setViews()

        db.tagsDao().getAllTags().observe(this, Observer<List<Tags>> {
            recyclerView.adapter = TagRecyclerViewAdapter(this, it)
        })
    }

    private fun onClickListener(view: View): View.OnClickListener {
        when (view.id) {
            R.id.add_tag_add_imageButton -> {
                return View.OnClickListener {
                    var flg = true
                    if (addEditText.text.toString().isEmpty()) {
                        Toast.makeText(this, "追加するタグ名を入力してください。", Toast.LENGTH_SHORT).show()
                    } else {

                        db.tagsDao().getCountByName(addEditText.text.toString()).observe(this, Observer<Int> {
                            if (it != null) {
                                if (it == 0) {
                                    val tag = Tags().apply {
                                        this.name = addEditText.text.toString()
                                        this.color = Color.GREEN
                                    }
                                    thread {
                                        flg = false
                                        db.tagsDao().insert(tag)
                                    }
                                    recyclerView.adapter!!.notifyItemInserted(recyclerView.adapter!!.itemCount)
                                    recyclerView.adapter!!.notifyItemChanged(recyclerView.adapter!!.itemCount - 1, tag)
                                    Toast.makeText(this, "タグを追加しました。", Toast.LENGTH_SHORT).show()
                                } else if (flg) {
                                    Toast.makeText(this, "その名前のタグは存在しています。", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                }
            }
            R.id.add_tag_add_editText -> {
                return View.OnClickListener {

                }
            }
        }
        return View.OnClickListener { }
    }

    private fun setViews(){
        addImageButton = findViewById(R.id.add_tag_add_imageButton)
        addImageButton.setOnClickListener(onClickListener(addImageButton))
        addEditText = findViewById(R.id.add_tag_add_editText)
        addEditText.setOnClickListener(onClickListener(addEditText))
        recyclerView = findViewById(R.id.add_tag_recyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager=layoutManager
    }
}