package com.apppppp.tryspringanimation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            val target = findViewById<View>(R.id.imageView)
            doSpringAnimation(target)
        }

    }

    fun doSpringAnimation(sender: View) {

        sender.scaleX = 0.5f
        sender.scaleY = 0.5f

        val (anim1X, anim1Y ) = sender.let { view ->
            SpringAnimation(view, DynamicAnimation.SCALE_X, 1.0f ) to
                    SpringAnimation(view, DynamicAnimation.SCALE_Y, 1.0f)
        }
        anim1X.spring.apply {
            dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
            stiffness = SpringForce.STIFFNESS_LOW
        }
        anim1Y.spring.apply {
            dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
            stiffness = SpringForce.STIFFNESS_LOW
        }
        anim1X.start()
        anim1Y.start()

    }
}
