package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.AddScheduleTabAdapter
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.TabAdapter
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import kotlinx.android.synthetic.main.activity_add_schedule.*
import kotlinx.android.synthetic.main.activity_main.*

class AddScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        setTabLayout()

    }

    private fun setTabLayout() {
        val adapter = AddScheduleTabAdapter(supportFragmentManager, this)
        val viewPager = findViewById<ViewPager>(R.id.add_schedule_viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.add_schedule_tabLayout)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        for(i in 0 until adapter.count){
            val tab =tabLayout.getTabAt(i)!!
            tab.customView = adapter.getTabView(tabLayout,i)
        }
    }
}