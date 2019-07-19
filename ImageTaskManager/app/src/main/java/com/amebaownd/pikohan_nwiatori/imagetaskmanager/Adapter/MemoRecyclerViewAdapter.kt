package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity.MemoInfoActivity
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.MemosAndMemoTags
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R

interface GetTagName {
    fun getTagNameById(tagId: Long, textView: TextView)
}

class MemoRecyclerViewAdapter(context: Context, private val memosAndMemoTags: List<MemosAndMemoTags>?) :
    RecyclerView.Adapter<MemoRecyclerViewAdapter.MemoListViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    var lisener: GetTagName? = null
    lateinit var view: View
    override fun onCreateViewHolder(parent: ViewGroup, viewTyape: Int): MemoListViewHolder {
        view = inflater.inflate(R.layout.memo_card, parent, false)
        return MemoListViewHolder(view)
    }

    override fun getItemCount() = if (memosAndMemoTags != null) memosAndMemoTags.size else 0

    override fun onBindViewHolder(viewHolder: MemoListViewHolder, position: Int) {
        viewHolder.title.text = memosAndMemoTags!![position].memos.title
        viewHolder.created.text = SimpleDateFormat("MM/dd  HH:mm").format(memosAndMemoTags[position].memos.created)
        viewHolder.card.setOnClickListener {
            val intent = Intent(view.context, MemoInfoActivity::class.java)
            intent.putExtra("memoId", memosAndMemoTags[position].memos.memoId)
            view.context.startActivity(intent)
        }

        for (i in memosAndMemoTags[position].memoTags.indices) {
            val tag = inflater.inflate(R.layout.tag, viewHolder.tagLayout, false)
            if (lisener != null)
                    lisener!!.getTagNameById(memosAndMemoTags[position].memoTags[i].tagId,tag.findViewById<TextView>(R.id.tag_textView))
            viewHolder.tagLayout.addView(tag)
        }
    }

    class MemoListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card = view.findViewById<CardView>(R.id.memo_card_cardView)
        val title = view.findViewById<TextView>(R.id.memo_card_title_textView)
        val created = view.findViewById<TextView>(R.id.memo_card_created)
        val tagLayout = view.findViewById<LinearLayout>(R.id.memo_card_tag_layout)
    }

    fun setListener(listener: GetTagName) {
        this.lisener = listener
    }
}