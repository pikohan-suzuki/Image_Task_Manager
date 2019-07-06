package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity

import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import com.google.android.flexbox.FlexboxLayout
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

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

        db.memosDao().loadMemosAndMemoImagesByMemoId(memoId).observe(this, Observer {
            for (i in it!!.memoImages.indices) {
                val uri = Uri.parse(it.memoImages[i].uri)
                try {
                val path : String? = getPathFromUri(this,uri)
                val file = File(path)
                val inputStream = FileInputStream(file)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        val imageView = ImageView(this)
                        imageView.setImageBitmap(bitmap)
                        imageView.id = View.generateViewId()
                        imageIdList.add(imageView.id)
                        imagesLayout.addView(imageView)

                        val constraintSet = ConstraintSet()
                        constraintSet.clone(imagesLayout)
                        if (imageIdList.size == 1)
                            constraintSet.connect(imageView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 50)
                        else
                            constraintSet.connect(imageView.id, ConstraintSet.TOP, imageIdList[imageIdList.lastIndex - 1], ConstraintSet.BOTTOM, 50)
                        constraintSet.applyTo(imagesLayout)
                    }
                }catch (e:Exception){
                   Toast.makeText(this, e.printStackTrace().toString(), Toast.LENGTH_SHORT).show()
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


    fun getPathFromUri(context: Context, uri: Uri): String? {
        val isAfterKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        Log.e("getPathFromUri", "uri:" + uri.authority!!)
        if (isAfterKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents" == uri.authority) {// ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true))
                {
                    return (Environment.getExternalStorageDirectory().path + "/" + split[1])
                } else
                {
                    return  "/stroage/" + type + "/" + split[1]
                }
            } else if ("com.android.providers.downloads.documents" == uri.authority) {// DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if ("com.android.providers.media.documents" == uri.authority) {// MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                var contentUri: Uri? = MediaStore.Files.getContentUri("external")
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {//MediaStore
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {// File
            return uri.path
        }
        return null
    }
    // 下ごしらえ③-2 変換関数
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val cindex = cursor.getColumnIndexOrThrow(projection[0])
                return cursor.getString(cindex)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }
}