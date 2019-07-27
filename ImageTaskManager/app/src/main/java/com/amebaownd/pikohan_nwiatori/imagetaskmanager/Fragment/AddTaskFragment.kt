package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.persistence.room.Room
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.*
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import com.google.android.flexbox.FlexboxLayout
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread

class AddTaskFragment() : Fragment() {
    private lateinit var db: AppDatabase
    private lateinit var inflater: LayoutInflater

    lateinit var deadlineEditText: EditText
    lateinit var titleEditText: EditText
    lateinit var tagEditText: AppCompatAutoCompleteTextView
    lateinit var addTaskButton: Button
    lateinit var addImageButton: ImageButton
    lateinit var memoEditText: EditText
    lateinit var imageConstraintLayout: ConstraintLayout
    lateinit var tagLayout:FlexboxLayout

    private var selectedImagesId = mutableListOf<Int>()
    private var selectedImagesUri = mutableListOf<Uri>()
    private var tagIdList= mutableListOf<Long>()
    private var tagViewIdList = mutableListOf<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_task, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = Room.databaseBuilder(view.context, AppDatabase::class.java, "database").build()
        inflater = LayoutInflater.from(view.context)
        setViews()
    }

    private fun setViews() {
        deadlineEditText = view!!.findViewById(R.id.add_task_deadline_editText)
        titleEditText = view!!.findViewById(R.id.add_task_title_editText)
        tagEditText = view!!.findViewById(R.id.add_task_search_tags_editText)
        tagLayout = view!!.findViewById(R.id.add_task_show_tags_flexboxLayout)
        addTaskButton = view!!.findViewById(R.id.add_task_add_task_button)
        addTaskButton.setOnClickListener(setOnClickListener(addTaskButton))
        addImageButton = view!!.findViewById(R.id.add_task_add_image_imageButton)
        addImageButton.setOnClickListener(setOnClickListener(addImageButton))
        memoEditText = view!!.findViewById(R.id.add_task_memo_editText)
        imageConstraintLayout = view!!.findViewById(R.id.add_task_image_layout)
        val today = Calendar.getInstance()
        deadlineEditText.setOnClickListener {
            DatePickerDialog(
                view!!.context,
                listener,
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            )
        }
        setSearchTagAdapter()

        tagEditText.setOnItemClickListener { adapterView, view, position, id ->
            db.tagsDao().getTagByName((view as TextView).text.toString()).observe(viewLifecycleOwner, android.arch.lifecycle.Observer {
                if (!tagIdList.contains(it!!.tagId)) {
                    tagIdList.add(it.tagId)
                    setTags(it.tagId)
                }
            })
        }
    }
    private fun setTags(tagId: Long) {
        db.tagsDao().getTagByTagId(tagId).observe(this, android.arch.lifecycle.Observer {
            val tagView = inflater.inflate(R.layout.tag, tagLayout, false)
            tagView.findViewById<TextView>(R.id.tag_textView).text = it!!.name
            tagView.id = View.generateViewId()
            tagViewIdList.add(tagView.id)
            tagView.setOnClickListener(setOnClickListener(tagView))
            tagLayout.addView(tagView)
        })
    }

    private fun setOnClickListener(view: View): View.OnClickListener {
        when (view.id) {
            R.id.add_task_add_task_button -> {
                return View.OnClickListener {
                    if (validation()) {
                        thread {

                            val time = System.currentTimeMillis()
                            val task = Tasks().apply {
                                title = titleEditText.text.toString()
                                date = time
                            }
                            val id = db.tasksDao().insert(task)
                            for (i in selectedImagesUri.indices) {
                                val taskImages = TaskImages().apply {
                                    this.order = i + 1
                                    this.taskId = id
                                    this.uri = selectedImagesUri[i].toString()
                                }
                                db.taskImagesDao().insert(taskImages)
                            }
                            for (i in tagIdList.indices) {
                                val tag = TaskTags().apply {
                                    this.taskId = id
                                    this.tagId = tagIdList[i]
                                }
                                db.taskTagsDao().insert(tag)
                            }
                        }
                        Toast.makeText(view!!.context,"タスクを追加しました。",Toast.LENGTH_SHORT).show()
                    }
                }
            }


            R.id.add_task_add_image_imageButton -> {
                return View.OnClickListener {
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "image/*"
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    startActivityForResult(intent, 101)
                }
            }
            else -> {

                return View.OnClickListener {
                    val num = tagViewIdList.indexOf(view.id)
                    tagIdList.removeAt(num)
                    tagViewIdList.removeAt(num)
                    tagLayout.removeView(it)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            if (data != null) {
                if (data.data != null) {
                    val uri = data.data
                    try {
                        val image = MediaStore.Images.Media.getBitmap(view!!.context.contentResolver, uri)
                        val imageView = ImageView(view!!.context)
                        imageView.id = View.generateViewId()
                        imageView.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                        imageView.setImageBitmap(image)


                        imageConstraintLayout.addView(imageView)
                        selectedImagesId.add(imageView.id)
                        selectedImagesUri.add(uri)
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(imageConstraintLayout)
                        if (selectedImagesId.size == 1)
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
                                selectedImagesId[selectedImagesId.lastIndex - 1],
                                ConstraintSet.BOTTOM,
                                50
                            )

                        constraintSet.connect(
                            imageView.id,
                            ConstraintSet.LEFT,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.LEFT,
                            50
                        )

                        constraintSet.connect(
                            addImageButton.id,
                            ConstraintSet.TOP,
                            selectedImagesId[selectedImagesId.lastIndex],
                            ConstraintSet.BOTTOM,
                            20
                        )
                        constraintSet.applyTo(imageConstraintLayout)

                    } catch (e: IOException) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private val listener =
        DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val str = String.format(Locale.JAPAN, "%d/%d/%d", year, monthOfYear, dayOfMonth)
            deadlineEditText.setText(str)
        }

    private fun setSearchTagAdapter() {
        db.tagsDao().getAllTagsName().observe(viewLifecycleOwner, android.arch.lifecycle.Observer { it ->
            tagEditText.setAdapter(ArrayAdapter(view!!.context, android.R.layout.simple_list_item_1, it))
        })
    }

    private fun validation(): Boolean {
        var str = "メモを追加できませんでした。"
        var flg = true
        if (titleEditText.text.isEmpty() || titleEditText.text.isBlank()) {
            str += "\n ・タイトルを入力してください"
            flg = false
        }
        if (deadlineEditText.text.isEmpty() || deadlineEditText.text.isBlank()) {
            str += "\n ・期限を入力してください"
            flg = false
        }
        if (tagIdList.size == 0) {
            str += "\n ・1つ以上のタグを選択してください"
            flg = false
        }
        if (!flg)
            Toast.makeText(view!!.context, str, Toast.LENGTH_SHORT).show()
        return flg
    }
}
