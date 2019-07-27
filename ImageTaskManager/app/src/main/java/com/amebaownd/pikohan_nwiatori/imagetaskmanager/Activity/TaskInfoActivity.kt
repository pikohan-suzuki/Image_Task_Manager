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

class TaskInfoActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var inflater: LayoutInflater

    lateinit var imagesLayout: ConstraintLayout
    lateinit var addImageButton: ImageButton

    lateinit var titleTextView: TextView
    lateinit var titleEditText: EditText
    lateinit var dateTextView: TextView
    lateinit var dateEditText: EditText
    lateinit var searchTagEditText: AutoCompleteTextView
    lateinit var tagLayout: FlexboxLayout
    lateinit var memoTextView: TextView
    lateinit var memoEditText: EditText

    lateinit var editImageButton: ImageButton
    lateinit var doneImageButton: ImageButton

    private var imageUriList = mutableListOf<String>()
    private var imageIdList = mutableListOf<Int>()
    private var deleteButtonList = mutableListOf<ImageButton>()
    private var tagIdList = mutableListOf<Long>()
    private var tagViewIdList = mutableListOf<Int>()

    lateinit var taskAndTaskTags: TasksAndTaskTags
    lateinit var taskAndTaskImages: TasksAndTaskImages

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_info)
        db = Room.databaseBuilder(this, AppDatabase::class.java, "database").build()
        inflater = LayoutInflater.from(this)
        setViews()
        setSearchTagAdapter()
        setListener()
        val taskId = intent.getLongExtra("taskId", 0)
        if (taskId != 0L) {
            db.tasksDao().loadTaskAndTaskTagsByIdSortByCreated(taskId).observe(this, Observer {
                taskAndTaskTags = it!![0]
                tagLayout.removeAllViews()
                for (i in it!![0].taskTags.indices) {
                    tagIdList.add(it!![0].taskTags[i].tagId)
                    setTags(it!![0].taskTags[i].tagId)
                }
                setTitleAndMemo(it!![0])
            })
            db.tasksDao().loadTaskAndTaskImagesByIdSortByCreated(taskId).observe(this, Observer {
                taskAndTaskImages = it!![0]
                setImages(it!![0])
            })
        }


    }

    private fun setViews() {
        imagesLayout = findViewById(R.id.task_info_image_layout)
        addImageButton = findViewById(R.id.task_info_add_image_imageButton)
        titleTextView = findViewById(R.id.task_info_title_textView)
        titleEditText = findViewById(R.id.task_info_title_editText)
        dateTextView = findViewById(R.id.task_info_deadline_textView)
        dateEditText = findViewById(R.id.task_info_deadline_editText)
        searchTagEditText = findViewById(R.id.task_info_search_tags_editText)
        tagLayout = findViewById(R.id.task_info_show_tags_linearLayout)
        memoTextView = findViewById(R.id.task_info_memo_textView)
        memoEditText = findViewById(R.id.task_info_memo_editText)

        editImageButton = findViewById(R.id.task_info_edit_imageButton)
        doneImageButton = findViewById(R.id.task_info_done_imageButton)

        addImageButton = ImageButton(this)
        addImageButton.setImageResource(R.drawable.ic_add_box_black_24dp)
        addImageButton.background = getDrawable(R.color.colorAlphaMax)
        addImageButton.id = View.generateViewId()
        addImageButton.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addImageButton.visibility = View.INVISIBLE
    }

    private fun setListener() {
        editImageButton.setOnClickListener(onClickListener(editImageButton, false))
        doneImageButton.setOnClickListener(onClickListener(doneImageButton, false))
        addImageButton.setOnClickListener(onClickListener(addImageButton, false))
        searchTagEditText.setOnItemClickListener { adapterView, view, position, id ->
            db.tagsDao().getTagByName((view as TextView).text.toString()).observe(this, Observer {
                if (!tagIdList.contains(it!!.tagId)) {
                    tagIdList.add(it.tagId)
                    setTags(it.tagId)
                }
            })
        }
    }

    private fun setSearchTagAdapter() {
        db.tagsDao().getAllTagsName().observe(this, Observer {
            searchTagEditText.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, it))
        })
    }

    private fun setTitleAndMemo(tasksAndTaskTags: TasksAndTaskTags) {
        titleEditText.setText(tasksAndTaskTags!!.task.title)
        titleTextView.text = tasksAndTaskTags!!.task.title
        dateTextView.text = getString(
            R.string.add_memo_created,
            SimpleDateFormat("MM").format(tasksAndTaskTags.task.date).toInt(),
            SimpleDateFormat("dd").format(tasksAndTaskTags.task.date).toInt(),
            SimpleDateFormat("HH").format(tasksAndTaskTags.task.date).toInt(),
            SimpleDateFormat("mm").format(tasksAndTaskTags.task.date).toInt()
        )
        dateEditText.setText(
            getString(
                R.string.add_memo_created,
                SimpleDateFormat("MM").format(tasksAndTaskTags.task.date).toInt(),
                SimpleDateFormat("dd").format(tasksAndTaskTags.task.date).toInt(),
                SimpleDateFormat("HH").format(tasksAndTaskTags.task.date).toInt(),
                SimpleDateFormat("mm").format(tasksAndTaskTags.task.date).toInt()
            )
        )
        memoEditText.setText(tasksAndTaskTags.task.memo)
        memoTextView.text = tasksAndTaskTags.task.memo
    }

    private fun setImages(tasksAndTaskImages: TasksAndTaskImages) {
        imagesLayout.removeAllViews()
        imagesLayout.addView(addImageButton)
        imageUriList = mutableListOf()
        val constraintSet = ConstraintSet()
        constraintSet.clone(imagesLayout)
        top2Top(constraintSet, addImageButton.id, ConstraintSet.PARENT_ID, 0)
        imagesLayout.setConstraintSet(constraintSet)
        for (i in tasksAndTaskImages!!.taskImages.indices) {
            val uri = Uri.parse(tasksAndTaskImages.taskImages[i].uri)
            imageUriList.add(tasksAndTaskImages.taskImages[i].uri)
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
                    imageView.layoutParams =
                        ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    imageView.scaleType = ImageView.ScaleType.FIT_CENTER
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

    private fun onClickListener(view: View, isTagView: Boolean): View.OnClickListener {
        when (view.id) {
            R.id.task_info_edit_imageButton -> {
                return View.OnClickListener {
                    it.visibility = View.INVISIBLE
                    doneImageButton.visibility = View.VISIBLE
                    titleEditText.visibility = View.VISIBLE
                    titleTextView.visibility = View.INVISIBLE
                    memoEditText.visibility = View.VISIBLE
                    memoTextView.visibility = View.INVISIBLE
                    addImageButton.visibility = View.VISIBLE
                    deleteButtonList.forEach { imageButton -> imageButton.visibility = View.VISIBLE }
                    searchTagEditText.visibility = View.VISIBLE
                    isEditMode = true
                }
            }
            R.id.task_info_done_imageButton -> {
                return View.OnClickListener {
                    if (validation()) {
                        updateTask()
                        it.visibility = View.INVISIBLE
                        editImageButton.visibility = View.VISIBLE
                        titleEditText.visibility = View.INVISIBLE
                        titleTextView.visibility = View.VISIBLE
                        memoEditText.visibility = View.INVISIBLE
                        memoTextView.visibility = View.VISIBLE
                        addImageButton.visibility = View.INVISIBLE
                        deleteButtonList.forEach { imageButton -> imageButton.visibility = View.INVISIBLE }
                        searchTagEditText.visibility = View.GONE
                        isEditMode = false
                    }
                }
            }
            addImageButton.id -> {
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
                                    top2Top(constraintSet, addImageButton.id, ConstraintSet.PARENT_ID, 0)
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
    private fun updateTask() {
        val tasks = taskAndTaskTags.task
        tasks.title = titleEditText.text.toString()
        tasks.memo = memoEditText.text.toString()
        val taskTags = mutableListOf<TaskTags>()
        for (i in tagIdList.indices)
            taskTags.add(TaskTags(tasks.taskId, tagIdList[i]))
        val taskImages = mutableListOf<TaskImages>()
        for (i in imageUriList.indices)
            taskImages.add(TaskImages(tasks.taskId, i, imageUriList[i]))
        val taskAndTaskTags = TasksAndTaskTags()
        taskAndTaskTags.task = tasks
        taskAndTaskTags.taskTags = taskTags
        setTitleAndMemo(taskAndTaskTags)
        tagLayout.removeAllViews()
        for (i in taskTags.indices)
            setTags(taskTags[i].tagId)
        val TasksAndTaskImages = TasksAndTaskImages()
        TasksAndTaskImages.task = tasks
        TasksAndTaskImages.taskImages = taskImages
        setImages(TasksAndTaskImages)

        thread {
            db.taskTagsDao().update(tasks.taskId,taskTags)
            db.taskImagesDao().update(tasks.taskId, taskImages)
            db.tasksDao().update(tasks)
        }
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