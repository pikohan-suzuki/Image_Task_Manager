package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.AddScheduleTabAdapter
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Data.AppDatabase
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import kotlinx.android.synthetic.main.activity_main.*

class AddScheduleActivity : AppCompatActivity() {

    lateinit var db: AppDatabase
    lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        db = Room.databaseBuilder(this, AppDatabase::class.java, "database").build()

        setTabLayout()
        val isTask = intent.getIntExtra("is_task", 1)
        if (isTask == 0) {
            tabLayout.getTabAt(1)!!.select()
        }
    }

    private fun setTabLayout() {
        val adapter = AddScheduleTabAdapter(supportFragmentManager, this)
        val viewPager = findViewById<ViewPager>(R.id.add_schedule_viewPager)
        tabLayout = findViewById<TabLayout>(R.id.add_schedule_tabLayout)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        for (i in 0 until adapter.count) {
            val tab = tabLayout.getTabAt(i)!!
            tab.customView = adapter.getTabView(tabLayout, i)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}