package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.*
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.Tags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import com.google.android.flexbox.FlexboxLayout
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

class MemoInfoActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var inflater: LayoutInflater

    private lateinit var editImageButton: ImageButton
    private lateinit var doneImageButton: ImageButton

    private lateinit var titleEditText: EditText
    private lateinit var titleTextView: TextView
    private lateinit var createdTextView: TextView
    private lateinit var memoEditText: EditText
    private lateinit var memoTextView: TextView
    private lateinit var tagLayout: FlexboxLayout
    private lateinit var imagesLayout: ConstraintLayout
    private lateinit var addImageButton: ImageButton

    private var imageUriList = mutableListOf<String>()
    private var imageIdList = mutableListOf<Int>()
    private var tagIdList = mutableListOf<Long>()
    private var tagViewIdList = mutableListOf<Int>()
    private var deleteButtonIdList = mutableListOf<Int>()

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_info)

        val memoId = intent.extras.getLong("memoId")
        val tagIdArray = intent.extras.getLongArray("tagIdList")
        for (i in tagIdArray.indices)
            tagIdList.add(tagIdArray[i])

        inflater = LayoutInflater.from(this)
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database").build()
        setViews()
        setListener()

        db.memosDao().loadMemosAndMemoTagsByMemoId(memoId).observe(this, Observer {
            titleEditText.setText(it!!.memos.title)
            titleTextView.text = it!!.memos.title
            createdTextView.text = getString(
                R.string.add_memo_created,
                SimpleDateFormat("MM").format(it.memos.created).toInt(),
                SimpleDateFormat("dd").format(it.memos.created).toInt(),
                SimpleDateFormat("HH").format(it.memos.created).toInt(),
                SimpleDateFormat("mm").format(it.memos.created).toInt()
            )
            memoEditText.setText(it.memos.memo)
            memoTextView.text = it.memos.memo
        })

        for (i in tagIdList!!.indices) {
            db.tagsDao().getTagByTagId(tagIdList!![i]).observe(this, Observer {
                val tagView = inflater.inflate(R.layout.tag, tagLayout, false)
                tagView.findViewById<TextView>(R.id.tag_textView).text = it!!.name
                tagView.id = View.generateViewId()
                tagViewIdList.add(tagView.id)
                tagView.setOnClickListener(onClickListener(tagView, true))
                tagLayout.addView(tagView)
            })
        }

        db.memosDao().loadMemosAndMemoImagesByMemoId(memoId).observe(this, Observer {
            for (i in it!!.memoImages.indices) {
                val uri = Uri.parse(it.memoImages[i].uri)
                imageUriList.add(it.memoImages[i].uri)
                try {
                    val path: String? = getPathFromUri(this, uri)
                    val file = File(path)
                    val inputStream = FileInputStream(file)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        val imageView = ImageView(this)
                        imageView.setImageBitmap(bitmap)
                        imageView.id = View.generateViewId()
                        imageIdList.add(imageView.id)
                        imagesLayout.addView(imageView)
                        val deleteImageButton = ImageButton(this)
                        deleteImageButton.setImageResource(R.drawable.ic_delete_black_24dp)
                        deleteImageButton.background=getDrawable(R.color.colorAlphaMax)
                        deleteImageButton.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                        deleteImageButton.id=View.generateViewId()
                        deleteButtonIdList.add(deleteImageButton.id)
                        deleteImageButton.visibility=View.INVISIBLE
                        deleteImageButton.setOnClickListener(onClickListener(deleteImageButton,false))
                        imagesLayout.addView(deleteImageButton)

                        val constraintSet = ConstraintSet()
                        constraintSet.clone(imagesLayout)
                        if (imageIdList.size == 1)
                            constraintSet.connect(
                                imageView.id,
                                ConstraintSet.TOP,
                                ConstraintSet.PARENT_ID,
                                ConstraintSet.TOP,
                                50
                            )
                        else
                            constraintSet.connect(
                                imageView.id,
                                ConstraintSet.TOP,
                                imageIdList[imageIdList.lastIndex - 1],
                                ConstraintSet.BOTTOM,
                                50
                            )
                        constraintSet.connect(
                            deleteImageButton.id,
                            ConstraintSet.TOP,
                            imageView.id,
                            ConstraintSet.TOP,
                            0
                        )
                        constraintSet.connect(
                            deleteImageButton.id,
                            ConstraintSet.LEFT,
                            imageView.id,
                            ConstraintSet.RIGHT,
                            10
                        )

                        constraintSet.connect(
                            addImageButton.id,
                            ConstraintSet.TOP,
                            imageIdList[imageIdList.lastIndex],
                            ConstraintSet.BOTTOM,
                            50
                        )
                        constraintSet.applyTo(imagesLayout)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, e.printStackTrace().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setViews() {
        editImageButton = findViewById(R.id.memo_info_edit_imageButton)
        doneImageButton = findViewById(R.id.memo_info_done_imageButton)

        titleEditText = findViewById(R.id.memo_info_title_editText)
        titleTextView = findViewById(R.id.memo_info_title_textVIew)
        createdTextView = findViewById(R.id.memo_info_created_textView)
        memoEditText = findViewById(R.id.memo_info_memo_editText)
        memoTextView = findViewById(R.id.memo_info_memo_textView)
        tagLayout = findViewById(R.id.memo_info_show_tags_flexboxLayout)
        imagesLayout = findViewById(R.id.memo_info_image_layout)
        addImageButton = findViewById(R.id.memo_info_add_image_imageButton)
    }

    private fun setListener() {
        editImageButton.setOnClickListener(onClickListener(editImageButton, false))
        doneImageButton.setOnClickListener(onClickListener(doneImageButton, false))
        addImageButton.setOnClickListener(onClickListener(addImageButton,false))
    }

    private fun onClickListener(view: View, isTagView: Boolean): View.OnClickListener {
        when (view.id) {
            R.id.memo_info_edit_imageButton -> {
                return View.OnClickListener {
                    it.visibility = View.INVISIBLE
                    doneImageButton.visibility = View.VISIBLE
                    titleEditText.visibility = View.VISIBLE
                    titleTextView.visibility = View.INVISIBLE
                    memoEditText.visibility = View.VISIBLE
                    memoTextView.visibility = View.INVISIBLE
                    addImageButton.visibility = View.VISIBLE
                    deleteButtonIdList.forEach{
                        findViewById<ImageButton>(it).visibility=View.VISIBLE
                    }
                    isEditMode = true
                }
            }
            R.id.memo_info_done_imageButton -> {
                return View.OnClickListener {
                    it.visibility = View.INVISIBLE
                    editImageButton.visibility = View.VISIBLE
                    titleEditText.visibility = View.INVISIBLE
                    titleTextView.visibility = View.VISIBLE
                    memoEditText.visibility = View.INVISIBLE
                    memoTextView.visibility = View.VISIBLE
                    addImageButton.visibility = View.INVISIBLE
                    deleteButtonIdList.forEach{
                        findViewById<ImageButton>(it).visibility=View.INVISIBLE
                    }
                    isEditMode = false
                }
            }
            R.id.memo_info_add_image_imageButton -> {
                return View.OnClickListener {
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "image/*"
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    startActivityForResult(intent, 101)
                }
            }
            else -> {
                if (isTagView) {
                    return View.OnClickListener {
                        if (isEditMode) {
                            val num = tagViewIdList.indexOf(view.id)
                            tagIdList.removeAt(num)
                            tagViewIdList.removeAt(num)
                            tagLayout.removeView(it)

                    }
                }

                }else{
                    return View.OnClickListener {
                        if (isEditMode) {
                            val num = deleteButtonIdList.indexOf(it.id)
                            deleteButtonIdList.remove(it.id)
                            imagesLayout.removeView(findViewById(imageIdList[num]))
                            imageIdList.removeAt(num)
                            imageUriList.removeAt(num)
                            imagesLayout.removeView(it)

                            val constraintSet = ConstraintSet()
                            constraintSet.clone(imagesLayout)
                            if(imageIdList.size == num)
                                if(imageIdList.size==0)
                                    constraintSet.connect(addImageButton.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 50)
                                else
                                    constraintSet.connect(addImageButton.id, ConstraintSet.TOP, imageIdList[imageIdList.lastIndex], ConstraintSet.BOTTOM, 50)
                            else if(num==0){
                                if(imageIdList.size==0)
                                    constraintSet.connect(addImageButton.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 50)
                                else
                                    constraintSet.connect(imageIdList[0], ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 50)
                            }else{
                                constraintSet.connect(imageIdList[num], ConstraintSet.TOP, imageIdList[num-1], ConstraintSet.BOTTOM, 50)
                            }
                            constraintSet.applyTo(imagesLayout)
                        }
                    }
                }
            }
        }
        return View.OnClickListener { }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            if (data != null) {
                if (data.data != null) {
                    val uri = data.data
                    try {
                        val image = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        val imageView = ImageView(this)
                        imageView.id = View.generateViewId()
                        imageView.setImageBitmap(image)


                        imagesLayout.addView(imageView)
                        imageIdList.add(imageView.id)
                        imageUriList.add(uri.toString())
                        val deleteImageButton = ImageButton(this)
                        deleteImageButton.setImageResource(R.drawable.ic_delete_black_24dp)
                        deleteImageButton.background=getDrawable(R.color.colorAlphaMax)
                        deleteImageButton.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                        deleteImageButton.id=View.generateViewId()
                        deleteButtonIdList.add(deleteImageButton.id)
                        deleteImageButton.setOnClickListener(onClickListener(deleteImageButton,false))
                        imagesLayout.addView(deleteImageButton)
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(imagesLayout)
                        if (imageIdList.size == 1)
                            constraintSet.connect(
                                imageView.id,
                                ConstraintSet.TOP,
                                ConstraintSet.PARENT_ID,
                                ConstraintSet.TOP,
                                50
                            )
                        else
                            constraintSet.connect(
                                imageView.id,
                                ConstraintSet.TOP,
                                imageIdList[imageIdList.lastIndex - 1],
                                ConstraintSet.BOTTOM,
                                50
                            )
                        constraintSet.connect(
                            deleteImageButton.id,
                            ConstraintSet.TOP,
                            imageView.id,
                            ConstraintSet.TOP,
                            0
                        )
                        constraintSet.connect(
                            deleteImageButton.id,
                            ConstraintSet.LEFT,
                            imageView.id,
                            ConstraintSet.RIGHT,
                            10
                        )

                        constraintSet.connect(
                            addImageButton.id,
                            ConstraintSet.TOP,
                            imageIdList[imageIdList.lastIndex],
                            ConstraintSet.BOTTOM,
                            20
                        )
                        constraintSet.applyTo(imagesLayout)

                    } catch (e: IOException) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        val isAfterKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        Log.e("getPathFromUri", "uri:" + uri.authority!!)
        if (isAfterKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents" == uri.authority) {// ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return (Environment.getExternalStorageDirectory().path + "/" + split[1])
                } else {
                    return "/stroage/" + type + "/" + split[1]
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

    private fun getDataColumn(
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