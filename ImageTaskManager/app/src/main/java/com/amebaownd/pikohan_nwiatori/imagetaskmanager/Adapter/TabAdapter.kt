package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter

import android.content.Context
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Fragment.MemoListFragent
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Fragment.TaskListFragment
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R

class TabAdapter(fm: FragmentManager, private val context: Context) : FragmentStatePagerAdapter(fm) {

    private val inflater = LayoutInflater.from(context)
    private val title = arrayOf<String>("Task", "Memo")
    private val src = arrayOf<Int>(R.drawable.ic_local_mall_black_24dp,R.drawable.ic_comment_black_24dp)
    override fun getItem(position: Int): Fragment? {
        when(position){
            0->
                return TaskListFragment()
            1->
                return MemoListFragent()
        }
        return TaskListFragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }

    override fun getCount() = title.size

    fun getTabView(tabLayout: TabLayout, position: Int): View {
        val view = inflater.inflate(R.layout.tab_item, tabLayout, false)
        val tabTextView = view.findViewById<TextView>(R.id.tab_item_textView)
        val tabImageView = view.findViewById<ImageView>(R.id.tab_item_imageView)
        tabTextView.text = getPageTitle(position)
        tabTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tabImageView.setImageResource(src[position])
        return view
    }
}