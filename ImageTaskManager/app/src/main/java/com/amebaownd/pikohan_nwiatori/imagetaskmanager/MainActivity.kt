package com.amebaownd.pikohan_nwiatori.imagetaskmanager

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    lateinit var openButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttons: Array<ImageButton> = arrayOf(findViewById(R.id.add_tag_button),findViewById(R.id.add_memo_button),findViewById(R.id.add_task_button))
        openButton =findViewById(R.id.open_add_menu_button)
        openButton.setOnClickListener {
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
        visiable(buttons)
        for (i in array.indices) {
            val objectAnimator: ObjectAnimator =
                ObjectAnimator.ofPropertyValuesHolder(buttons[i], array[i][0], array[i][1])
            objectAnimator.setDuration(time)
            objectAnimator.start()
        }
    }
    

    private fun visiable(buttons:Array<ImageButton>){
        buttons.forEach {
            it.visibility = View.VISIBLE
        }
    }
}
