package com.apppppp.trythread

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    var mWorker: Thread? = null
    var running = AtomicBoolean(false)
    var counter:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.start).setOnClickListener {
            runWorker()
        }
        findViewById<Button>(R.id.stop).setOnClickListener {
            interruptWorker()
        }
    }

    fun runWorker() {
        running.set(true)
        mWorker = Thread {
            while (running.get()) {
                try {
                    Thread.sleep(1000)
                    counter++
                    println("counter $counter")
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
}
