package com.amebaownd.pikohan_nwiatori.imagetaskmanager

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    lateinit var openButton : ImageButton
    private var isOpen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttons: Array<ImageButton> = arrayOf(findViewById(R.id.add_tag_button),findViewById(R.id.add_memo_button),findViewById(R.id.add_task_button))
        openButton =findViewById(R.id.open_add_menu_button)
        openButton.setOnClickListener {
            if(isOpen)
                closeMenu(buttons)
            else
                openMenu(buttons)
        }


    }

    private fun openMenu(buttons:Array<ImageButton>){
        val radius = 300f
        val time: Long =300
        val array = Array(buttons.size,{ arrayOfNulls<PropertyValuesHolder>(2)})
        array[0][0] = PropertyValuesHolder.ofFloat("translationX", 0f, radius * cos(PI* 90 / 180).toFloat())
        array[0][1] = PropertyValuesHolder.ofFloat("translationY", 0f, -radius * sin(PI * 90 / 180).toFloat())
        array[1][0] = PropertyValuesHolder.ofFloat("translationX", 0f, radius * cos(PI * 135 / 180).toFloat())
        array[1][1] = PropertyValuesHolder.ofFloat("translationY", 0f, -radius * sin(PI * 135 / 180).toFloat())
        array[2][0] = PropertyValuesHolder.ofFloat("translationX", 0f, radius * cos(PI * 180 / 180).toFloat())
        array[2][1] = PropertyValuesHolder.ofFloat("translationY", 0f, -radius * sin(PI * 180 / 180).toFloat())
        val alpha = PropertyValuesHolder.ofFloat("alpha",0f,1f)
        for (i in array.indices) {
            visiable(buttons[i])
            val objectAnimator: ObjectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(buttons[i], array[i][0], array[i][1], alpha)
            objectAnimator.setDuration(time)
            objectAnimator.start()
        }
        isOpen=true
    }

    private fun closeMenu(buttons:Array<ImageButton>){
        val radius = 300f
        val time: Long =300
        val array = Array(buttons.size,{ arrayOfNulls<PropertyValuesHolder>(2)})
        array[0][0] = PropertyValuesHolder.ofFloat("translationX",  radius * cos(PI* 90 / 180).toFloat(),0f)
        array[0][1] = PropertyValuesHolder.ofFloat("translationY", -radius * sin(PI * 90 / 180).toFloat(),0f)
        array[1][0] = PropertyValuesHolder.ofFloat("translationX", radius * cos(PI * 135 / 180).toFloat(),0f)
        array[1][1] = PropertyValuesHolder.ofFloat("translationY",  -radius * sin(PI * 135 / 180).toFloat(),0f)
        array[2][0] = PropertyValuesHolder.ofFloat("translationX",  radius * cos(PI * 180 / 180).toFloat(),0f)
        array[2][1] = PropertyValuesHolder.ofFloat("translationY",  -radius * sin(PI * 180 / 180).toFloat(),0f)
        val alpha = PropertyValuesHolder.ofFloat("alpha",1f,0f)
        for (i in array.indices) {
            val objectAnimator: ObjectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(buttons[i], array[i][0], array[i][1],alpha)
            objectAnimator.setDuration(time)
            objectAnimator.start()
        }
        isOpen=false
    }
//
//    fun animateAlphaCloseMenu(menuLayout: ConstraintLayout) {
//        val time: Long = 300
//
//        val objectAnimator = ObjectAnimator.ofFloat(menuLayout, "alpha", 0.5f, 0f)
//        objectAnimator.setDuration(time)
//        objectAnimator.start()
////        gone(menuLayout)
//        isOpen = false
//    }
//    fun animateAlphaOpenMenu(menuLayout: ConstraintLayout) {
//        val time: Long = 300
//
//        val objectAnimator = ObjectAnimator.ofFloat(menuLayout, "alpha", 0f, 1f)
//        objectAnimator.setDuration(time)
//        objectAnimator.start()
////        gone(menuLayout)
//        isOpen = true
//    }

    private fun visiable(view:View){
        view.visibility = View.VISIBLE
    }
    private fun gone(view:View){
        view.visibility=View.GONE
    }
}
