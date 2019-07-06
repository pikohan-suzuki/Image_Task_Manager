package com.amebaownd.pikohan_nwiatori.imagetaskmanager.Activity

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.ImageButton
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.Adapter.TabAdapter
import com.amebaownd.pikohan_nwiatori.imagetaskmanager.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    private lateinit var openButton: ImageButton
    private lateinit var buttons: Array<ImageButton>
    private var isOpen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViews()
        setTabLayout()
    }

    private fun openMenu(buttons: Array<ImageButton>) {
        val radius = 300f
        val time: Long = 200
        val array = Array(buttons.size, { arrayOfNulls<PropertyValuesHolder>(2) })
        array[0][0] = PropertyValuesHolder.ofFloat("translationX", 0f, radius * cos(PI * 90 / 180).toFloat())
        array[0][1] = PropertyValuesHolder.ofFloat("translationY", 0f, -radius * sin(PI * 90 / 180).toFloat())
        array[1][0] = PropertyValuesHolder.ofFloat("translationX", 0f, radius * cos(PI * 135 / 180).toFloat())
        array[1][1] = PropertyValuesHolder.ofFloat("translationY", 0f, -radius * sin(PI * 135 / 180).toFloat())
        array[2][0] = PropertyValuesHolder.ofFloat("translationX", 0f, radius * cos(PI * 180 / 180).toFloat())
        array[2][1] = PropertyValuesHolder.ofFloat("translationY", 0f, -radius * sin(PI * 180 / 180).toFloat())
        val alpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        for (i in array.indices) {
            visiable(buttons[i])
            val objectAnimator: ObjectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(buttons[i], array[i][0], array[i][1], alpha)
            objectAnimator.setDuration(time)
            objectAnimator.start()
        }
        isOpen = true
    }

    private fun closeMenu(buttons: Array<ImageButton>) {
        val radius = 300f
        val time: Long = 200
        val array = Array(buttons.size, { arrayOfNulls<PropertyValuesHolder>(2) })
        array[0][0] = PropertyValuesHolder.ofFloat("translationX", radius * cos(PI * 90 / 180).toFloat(), 0f)
        array[0][1] = PropertyValuesHolder.ofFloat("translationY", -radius * sin(PI * 90 / 180).toFloat(), 0f)
        array[1][0] = PropertyValuesHolder.ofFloat("translationX", radius * cos(PI * 135 / 180).toFloat(), 0f)
        array[1][1] = PropertyValuesHolder.ofFloat("translationY", -radius * sin(PI * 135 / 180).toFloat(), 0f)
        array[2][0] = PropertyValuesHolder.ofFloat("translationX", radius * cos(PI * 180 / 180).toFloat(), 0f)
        array[2][1] = PropertyValuesHolder.ofFloat("translationY", -radius * sin(PI * 180 / 180).toFloat(), 0f)
        val alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
        for (i in array.indices) {
            val objectAnimator: ObjectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(buttons[i], array[i][0], array[i][1], alpha)
            objectAnimator.setDuration(time)
            objectAnimator.start()
        }
        isOpen = false
    }


    private fun visiable(view: View) {
        view.visibility = View.VISIBLE
    }

    private fun setTabLayout() {
        val adapter = TabAdapter(supportFragmentManager, this)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        for (i in 0 until adapter.count) {
            val tab = tabLayout.getTabAt(i)!!
            tab.customView = adapter.getTabView(tabLayout, i)
        }
    }
    private fun onClickListener(view: View): View.OnClickListener {
        when (view.id) {
            R.id.add_task_button -> {
                return View.OnClickListener {
                    val intent = Intent(this, AddScheduleActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.add_memo_button -> {
                return View.OnClickListener {
                    val intent = Intent(this, AddScheduleActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.add_tag_button -> {
                return View.OnClickListener {
                    val intent = Intent(this, AddTagActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.open_add_menu_button -> {
                return View.OnClickListener {
                    if (isOpen)
                        closeMenu(buttons)
                    else
                        openMenu(buttons)
                }
            }
        }
        return View.OnClickListener { }
    }

    private fun setViews(){
        buttons = arrayOf(
            findViewById(R.id.add_tag_button), findViewById(
                R.id.add_memo_button
            ), findViewById(R.id.add_task_button)
        )
        openButton = findViewById(R.id.open_add_menu_button)
        openButton.setOnClickListener(onClickListener(openButton))

        findViewById<ImageButton>(R.id.add_task_button).setOnClickListener(onClickListener(findViewById<ImageButton>(R.id.add_task_button)))
        findViewById<ImageButton>(R.id.add_memo_button).setOnClickListener(onClickListener(findViewById<ImageButton>(R.id.add_memo_button)))
        findViewById<ImageButton>(R.id.add_tag_button).setOnClickListener(onClickListener(findViewById<ImageButton>(R.id.add_tag_button)))
    }
}
