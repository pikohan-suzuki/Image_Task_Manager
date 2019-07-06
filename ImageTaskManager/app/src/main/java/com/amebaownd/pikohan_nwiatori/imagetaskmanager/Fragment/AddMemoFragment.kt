package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Fragment

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.content.ContentResolver
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.SearchTagsRecyclerViewAdapter
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.onSelectSearchedTags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.*
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import com.google.android.flexbox.FlexboxLayout
import java.io.IOException
import java.sql.Date
import java.sql.Time
import kotlin.concurrent.thread


class AddMemoFragment() : Fragment(), onSelectSearchedTags {
    override fun onSelectSearchedTags(tagsName: String) {
        db.tagsDao().getTagByName(tagsName)
            .observe(viewLifecycleOwner, Observer<Tags> {
                if (it != null) {
                    if (!selectedTagsId.contains(it.tagId)) {
                        selectedTagsId.add(it.tagId)
                        val view = inflater.inflate(R.layout.tag, null, false)
                        view.findViewById<TextView>(R.id.tag_textView).text = it.name

                        view.setOnClickListener {
                            selectedTagsId.removeAt(tagsFlexboxLayout.indexOfChild(view))
                            tagsFlexboxLayout.removeView(view)
                        }
                        tagsFlexboxLayout.addView(view)
                        recyclerView.adapter = SearchTagsRecyclerViewAdapter(view.context, null)
                        searchEditText.setText("")
                    }
                }
            })
    }

    private lateinit var createdTextView: TextView
    private lateinit var titleEditText: EditText
    private lateinit var db: AppDatabase
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var tagsFlexboxLayout: FlexboxLayout
    private var selectedTagsId = mutableListOf<Long>()
    private lateinit var inflater: LayoutInflater
    private lateinit var addImageButton: ImageButton
    private lateinit var imageConstraintLayout: ConstraintLayout
    private val selectedImagesId = mutableListOf<Int>()
    private val selectedImagesUri = mutableListOf<Uri>()
    private lateinit var addMemoButton: Button
    private lateinit var memoEditText: EditText
    //    private var inflater = LayoutInflater.from(this.context)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_memo, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Room.databaseBuilder(view.context, AppDatabase::class.java, "database").build()
        setViews(view)
    }

    private fun setViews(view: View) {
        recyclerView = view.findViewById(R.id.add_memo_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = SearchTagsRecyclerViewAdapter(view.context, null)
        inflater = LayoutInflater.from(this.context)
        imageConstraintLayout = view.findViewById(R.id.add_memo_image_layout)
        tagsFlexboxLayout = view.findViewById(R.id.add_memo_show_tags_flexBoxLayout)
        val time = System.currentTimeMillis()
        createdTextView = view.findViewById(R.id.add_memo_created_textView)
        createdTextView.text = getString(
            R.string.add_memo_created,
            SimpleDateFormat("MM").format(time).toInt(),
            SimpleDateFormat("dd").format(time).toInt(),
            SimpleDateFormat("HH").format(time).toInt(),
            SimpleDateFormat("mm").format(time).toInt()
        )
        searchEditText = view.findViewById(R.id.add_memo_search_tags_editText)
        searchEditText.addTextChangedListener(textChangedListener)

        addImageButton = view.findViewById(R.id.add_memo_add_image_imageButton)
        addImageButton.setOnClickListener(onClickListener(addImageButton))

        addMemoButton = view.findViewById(R.id.add_memo_add_memo_button)
        addMemoButton.setOnClickListener(onClickListener(addMemoButton))

        titleEditText = view.findViewById(R.id.add_memo_title_editText)
        memoEditText = view.findViewById(R.id.add_memo_memo_editText)
    }

    private val textChangedListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {
            if (count == 0) {
                val adapter = SearchTagsRecyclerViewAdapter(view!!.context, null)
                adapter.setOnSelectSearchedTags(this@AddMemoFragment)
                recyclerView.adapter = adapter
            } else {
                db.tagsDao().getTagsByKey("%" + str.toString() + "%")
                    .observe(viewLifecycleOwner, Observer<List<Tags>> {
                        val adapter = SearchTagsRecyclerViewAdapter(view!!.context, it)
                        adapter.setOnSelectSearchedTags(this@AddMemoFragment)
                        recyclerView.adapter = adapter
                    })
            }
        }
    }

    private fun onClickListener(view: View): View.OnClickListener {
        when (view.id) {
            R.id.add_memo_add_image_imageButton -> {
                return View.OnClickListener {
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "image/*"
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    startActivityForResult(intent, 101)
                }
            }
            R.id.add_memo_add_memo_button -> {
                return View.OnClickListener {
                    if (validation()) {
                        thread {

                            val time = System.currentTimeMillis()
                            val memo = Memos().apply {
                                title = titleEditText.text.toString()
                                created = time
                                memo = memoEditText.text.toString()
                            }
                            val id = db.memosDao().insert(memo)
                            for (i in selectedImagesUri.indices) {
                                val memoImages = MemoImages().apply {
                                    this.order = i + 1
                                    this.memoId = id
                                    this.uri = selectedImagesUri[i].toString()
                                }
                                db.memoImagesDao().insert(memoImages)
                            }
                            for(i in selectedTagsId.indices){
                                val tag =MemoTags().apply {
                                    this.memoId=id
                                    this.tagId=selectedTagsId[i]
                                }
                                db.memoTagsDao().insert(tag)

                            }
                        }
                        Toast.makeText(view.context, "メモを追加しました。", Toast.LENGTH_SHORT).show()
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
                        val image = MediaStore.Images.Media.getBitmap(view!!.context.contentResolver, uri)
                        val imageView = ImageView(view!!.context)
                        imageView.id = View.generateViewId()
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

    private fun validation(): Boolean {
        var str = "メモを追加できませんでした。"
        var flg = true
        if (titleEditText.text.isEmpty()) {
            str += "\n ・タイトルを入力してください"
            flg = false
        }
        if (selectedTagsId.size == 0) {
            str += "\n ・1つ以上のタグを選択してください"
            flg = false
        }
        if (!flg)
            Toast.makeText(view!!.context, str, Toast.LENGTH_SHORT).show()
        return flg
    }
}
