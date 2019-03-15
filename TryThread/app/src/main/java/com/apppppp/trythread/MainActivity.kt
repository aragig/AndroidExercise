package com.apppppp.trythread

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import java.util.concurrent.atomic.AtomicBoolean
import android.os.Looper



class MainActivity : AppCompatActivity() {

    var mWorker: Thread? = null
    var running = AtomicBoolean(false)
    var counter:Int = 0
    lateinit var mCounterTextView:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.start).setOnClickListener {
            runWorker()
        }
        findViewById<Button>(R.id.stop).setOnClickListener {
            interruptWorker()
        }
        mCounterTextView = findViewById(R.id.counter)
    }

    fun runWorker() {
        running.set(true)
        mWorker = Thread {
            while (running.get()) {
                try {
                    Thread.sleep(1000)
                    counter++
                    println("counter $counter")

                    postMainThread {
                        mCounterTextView.text = "$counter"
                    }


                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt()
                    println("Thread was interrupted, Failed to complete operation")
                }

            }
        }
        mWorker?.start()
    }

    fun interruptWorker() {
        running.set(false)
        mWorker?.interrupt()
    }


    fun postMainThread(action:()->Unit) {
        Handler(Looper.getMainLooper()).post(Runnable {
            action()
        })
    }
}
