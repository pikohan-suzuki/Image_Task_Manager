package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity

import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import com.google.android.flexbox.FlexboxLayout

class MemoInfoActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var inflater: LayoutInflater
    private lateinit var titleEditText: EditText
    private lateinit var createdTextView: TextView
    private lateinit var memoEditText: EditText
    private lateinit var tagLayout: FlexboxLayout
    private lateinit var imagesLayout:ConstraintLayout
    private var imageIdList = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_info)

        val memoId = intent.extras.getLong("memoId")
        val tagIdList = intent.extras.getLongArray("tagIdList")

        inflater = LayoutInflater.from(this)
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database").build()
        setViews()

        db.memosDao().loadMemosAndMemoTagsByMemoId(memoId).observe(this, Observer {
            titleEditText.setText(it!!.memos.title)
            createdTextView.text = getString(
                R.string.add_memo_created,
                SimpleDateFormat("MM").format(it.memos.created).toInt(),
                SimpleDateFormat("dd").format(it.memos.created).toInt(),
                SimpleDateFormat("HH").format(it.memos.created).toInt(),
                SimpleDateFormat("mm").format(it.memos.created).toInt()
            )
            memoEditText.setText(it.memos.memo)
        })

        for (i in tagIdList.indices) {
            db.tagsDao().getTagByTagId(tagIdList[i]).observe(this, Observer {
                val tagView = inflater.inflate(R.layout.tag, tagLayout, false)
                tagView.findViewById<TextView>(R.id.tag_textView).text = it!!.name
                tagLayout.addView(tagView)
            })
        }

        db.memosDao().loadMemosAndMemoImagessByMemoId(memoId).observe(this, Observer {
            for (i in it!!.memoImages.indices) {
                val uri = Uri.parse(it!!.memoImages[i].uri)
                val bitmap = getBitmapImage(uri)
                if(bitmap!=null){
                    val imageView =ImageView(this)
                    imageView.setImageBitmap(bitmap)
                    imageView.id = View.generateViewId()
                    imageIdList.add(imageView.id)

                    imagesLayout.addView(imageView)
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(imagesLayout)
                }
            }
        })
    }

    private fun setViews() {
        titleEditText = findViewById(R.id.memo_info_title_editText)
        createdTextView = findViewById(R.id.memo_info_created_textView)
        memoEditText = findViewById(R.id.memo_info_memo_editText)
        tagLayout = findViewById(R.id.memo_info_show_tags_flexboxLayout)
        imagesLayout= findViewById(R.id.memo_info_image_layout)
    }

    private fun getBitmapImage(uri:Uri): Bitmap? {
        var image: Bitmap?=null
        contentResolver.openFileDescriptor(uri, "r").use {
            image = BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }
        return image
    }
}