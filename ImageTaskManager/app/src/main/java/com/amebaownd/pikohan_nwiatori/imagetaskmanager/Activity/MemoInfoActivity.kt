package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
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
import android.view.ViewGroup
import android.widget.*
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.*
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import com.google.android.flexbox.FlexboxLayout
import java.io.BufferedInputStream
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
    private var deleteButtonList = mutableListOf<ImageButton>()

    private var isEditMode = false

    lateinit var memosAndMemoTags: MemosAndMemoTags
    lateinit var memosAndMemoImages: MemosAndMemoImages

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
            memosAndMemoTags = it!!
            setTitleAndMemo(it)
        })

        for (i in tagIdList!!.indices)
            setTags(tagIdList[i])


        db.memosDao().loadMemosAndMemoImagesByMemoId(memoId).observe(this, Observer {
            memosAndMemoImages = it!!
            setImages(it)
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
        addImageButton = ImageButton(this)
        addImageButton.setImageResource(R.drawable.ic_add_box_black_24dp)
        addImageButton.background=getDrawable(R.color.colorAlphaMax)
        addImageButton.id=View.generateViewId()
        addImageButton.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        addImageButton.visibility=View.INVISIBLE
    }

    private fun setListener() {
        editImageButton.setOnClickListener(onClickListener(editImageButton, false))
        doneImageButton.setOnClickListener(onClickListener(doneImageButton, false))
        addImageButton.setOnClickListener(onClickListener(addImageButton, false))
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
                    deleteButtonList.forEach { imageButton->
                        imageButton.visibility = View.VISIBLE
                    }
                    isEditMode = true
                }
            }
            R.id.memo_info_done_imageButton -> {
                return View.OnClickListener {
                    if (validation()) {
                        updateMemo()
                        it.visibility = View.INVISIBLE
                        editImageButton.visibility = View.VISIBLE
                        titleEditText.visibility = View.INVISIBLE
                        titleTextView.visibility = View.VISIBLE
                        memoEditText.visibility = View.INVISIBLE
                        memoTextView.visibility = View.VISIBLE
                        addImageButton.visibility = View.INVISIBLE
                        deleteButtonList.forEach { imageButton ->
                            imageButton.visibility = View.INVISIBLE
                        }
                        isEditMode = false
                    }
                }
            }
            addImageButton.id-> {
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

                } else {
                    return View.OnClickListener {
                        if (isEditMode) {
                            val num = deleteButtonList.indexOf(it)
                            deleteButtonList.remove(it)
                            imagesLayout.removeView(findViewById(imageIdList[num]))
                            imageIdList.removeAt(num)
                            imageUriList.removeAt(num)
                            imagesLayout.removeView(it)

                            val constraintSet = ConstraintSet()
                            constraintSet.clone(imagesLayout)
                            if (imageIdList.size == num)
                                if (imageIdList.size == 0)
                                    top2Top(constraintSet, addImageButton.id, ConstraintSet.PARENT_ID)
                                else
                                    top2Bottom(constraintSet, addImageButton.id, imageIdList[imageIdList.lastIndex])
                            else if (num == 0) {
                                if (imageIdList.size == 0)
                                    top2Top(constraintSet, addImageButton.id, ConstraintSet.PARENT_ID,0)
                                else
                                    top2Top(constraintSet, imageIdList[0], ConstraintSet.PARENT_ID)
                            } else
                                top2Bottom(constraintSet, imageIdList[num], imageIdList[num - 1])

                            constraintSet.applyTo(imagesLayout)
                        }
                    }
                }
            }
        }
        return View.OnClickListener { }
    }

    private fun updateMemo(){
        val memos = memosAndMemoTags.memos
        memos.title=titleEditText.text.toString()
        memos.memo =memoEditText.text.toString()
        val memoTags = mutableListOf<MemoTags>()
        for(i in tagIdList.indices)
            memoTags.add(MemoTags(memos.memoId,tagIdList[i]))
        val memoImages= mutableListOf<MemoImages>()
        for(i in imageUriList.indices)
            memoImages.add(MemoImages(memos.memoId,i,imageUriList[i]))
        val memosAndMemoTags=MemosAndMemoTags()
        memosAndMemoTags.memos=memos
        memosAndMemoTags.memoTags=memoTags
        setTitleAndMemo(memosAndMemoTags)
        tagLayout.removeAllViews()
        for(i in memoTags.indices)
            setTags(memoTags[i].tagId)
        val memosAndMemoImages=MemosAndMemoImages()
        memosAndMemoImages.memos=memos
        memosAndMemoImages.memoImages=memoImages
        setImages(memosAndMemoImages)

        thread {
            db.memoTagsDao().update(memos.memoId, memoTags)
            db.memoImagesDao().update(memos.memoId, memoImages)
            db.memosDao().update(memos)
        }
    }

    private fun setTitleAndMemo(memosAndMemoTags: MemosAndMemoTags) {
        titleEditText.setText(memosAndMemoTags!!.memos.title)
        titleTextView.text = memosAndMemoTags!!.memos.title
        createdTextView.text = getString(
            R.string.add_memo_created,
            SimpleDateFormat("MM").format(memosAndMemoTags.memos.created).toInt(),
            SimpleDateFormat("dd").format(memosAndMemoTags.memos.created).toInt(),
            SimpleDateFormat("HH").format(memosAndMemoTags.memos.created).toInt(),
            SimpleDateFormat("mm").format(memosAndMemoTags.memos.created).toInt()
        )
        memoEditText.setText(memosAndMemoTags.memos.memo)
        memoTextView.text = memosAndMemoTags.memos.memo
    }

    private fun setTags(tagId: Long) {
        db.tagsDao().getTagByTagId(tagId).observe(this, Observer {
            val tagView = inflater.inflate(R.layout.tag, tagLayout, false)
            tagView.findViewById<TextView>(R.id.tag_textView).text = it!!.name
            tagView.id = View.generateViewId()
            tagViewIdList.add(tagView.id)
            tagView.setOnClickListener(onClickListener(tagView, true))
            tagLayout.addView(tagView)
        })
    }

    private fun setImages(memosAndMemoImages: MemosAndMemoImages) {
        imagesLayout.removeAllViews()
        imagesLayout.addView(addImageButton)
        imageUriList = mutableListOf()
        val constraintSet = ConstraintSet()
        constraintSet.clone(imagesLayout)
        top2Top(constraintSet,addImageButton.id,ConstraintSet.PARENT_ID,0)
        imagesLayout.setConstraintSet(constraintSet)
        for (i in memosAndMemoImages!!.memoImages.indices) {
            val uri = Uri.parse(memosAndMemoImages.memoImages[i].uri)
            imageUriList.add(memosAndMemoImages.memoImages[i].uri)
            try {
                val path: String? = getPathFromUri(this, uri)
                val file = File(path)
                val inputStream = FileInputStream(file)
                val bitmap = BitmapFactory.decodeStream(BufferedInputStream(inputStream))
                inputStream.close()
                if (bitmap != null) {
                    val imageView = ImageView(this)
                    imageView.setImageBitmap(bitmap)
                    imageView.id = View.generateViewId()
                    imageView.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                    imageView.scaleType=ImageView.ScaleType.FIT_CENTER
                    imageIdList.add(imageView.id)
                    imagesLayout.addView(imageView)
                    val deleteImageButton = ImageButton(this)
                    deleteImageButton.setImageResource(R.drawable.ic_delete_black_24dp)
                    deleteImageButton.background = getDrawable(R.color.colorAlphaMax)
                    deleteImageButton.layoutParams =
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    deleteImageButton.id = View.generateViewId()
                    deleteButtonList.add(deleteImageButton)
                    deleteImageButton.visibility = View.INVISIBLE
                    deleteImageButton.setOnClickListener(onClickListener(deleteImageButton, false))
                    imagesLayout.addView(deleteImageButton)

                    val constraintSet = ConstraintSet()
                    constraintSet.clone(imagesLayout)
                    if (imageIdList.size == 1)
                        top2Top(constraintSet, imageView.id, ConstraintSet.PARENT_ID)
                    else
                        top2Bottom(constraintSet, imageView.id, imageIdList[imageIdList.lastIndex - 1])
                    top2Top(constraintSet, deleteImageButton.id, imageView.id, 0)
                    left2Right(constraintSet, deleteImageButton.id, imageView.id, 20)
                    top2Bottom(constraintSet, addImageButton.id, imageIdList[imageIdList.lastIndex])
                    constraintSet.applyTo(imagesLayout)
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.printStackTrace().toString(), Toast.LENGTH_SHORT).show()
            }
        }
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
                        deleteImageButton.background = getDrawable(R.color.colorAlphaMax)
                        deleteImageButton.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        deleteImageButton.id = View.generateViewId()
                        deleteButtonList.add(deleteImageButton)
                        deleteImageButton.setOnClickListener(onClickListener(deleteImageButton, false))
                        imagesLayout.addView(deleteImageButton)
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(imagesLayout)
                        if (imageIdList.size == 1)
                            top2Top(constraintSet, imageView.id, ConstraintSet.PARENT_ID)
                        else
                            top2Bottom(constraintSet, imageView.id, imageIdList[imageIdList.lastIndex - 1])
                        top2Top(constraintSet, deleteImageButton.id, imageView.id, 0)
                        left2Right(constraintSet, deleteImageButton.id, imageView.id, 20)
                        top2Bottom(constraintSet, addImageButton.id, imageIdList[imageIdList.lastIndex])
                        constraintSet.applyTo(imagesLayout)

                    } catch (e: IOException) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private fun validation(): Boolean {
        var flg = true
        var str = "編集を終了できません。"
        if (titleEditText.text.isEmpty()) {
            str += "\nタイトルを入力してください。"
            flg = false
        }
        if (tagIdList.isEmpty()) {
            str += "\nタグを１つ以上設定してください。"
            flg = false
        }
        return flg
    }

    private fun top2Top(constraintSet: ConstraintSet, childId: Int, parentId: Int, margin: Int = 50) {
        constraintSet.connect(
            childId,
            ConstraintSet.TOP,
            parentId,
            ConstraintSet.TOP,
            margin
        )
    }

    private fun top2Bottom(constraintSet: ConstraintSet, childId: Int, parentId: Int, margin: Int = 50) {
        constraintSet.connect(
            childId,
            ConstraintSet.TOP,
            parentId,
            ConstraintSet.BOTTOM,
            margin
        )
    }

    private fun left2Right(constraintSet: ConstraintSet, childId: Int, parentId: Int, margin: Int = 50) {
        constraintSet.connect(
            childId,
            ConstraintSet.LEFT,
            parentId,
            ConstraintSet.RIGHT,
            margin
        )
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