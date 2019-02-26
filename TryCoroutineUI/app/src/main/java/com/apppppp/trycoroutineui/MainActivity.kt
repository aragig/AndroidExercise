package com.apppppp.trycoroutineui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ActorScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup(hello, fab)
    }

//    fun setup(hello: TextView, fab: FloatingActionButton) {
//
//        fab.onClick {
//
//            for (i in 10 downTo 1) {
//                hello.text = "Countdown $i ..."
//                delay(500)
//            }
//            hello.text = "Done!"
//
//        }
//    }
//

    fun setup(hello: TextView, fab: FloatingActionButton) {
        var result = "none"

        GlobalScope.launch(Dispatchers.Main) {
            var counter = 0
            while (true) {
                hello.text = "${++counter}: $result"
                delay(100)
            }
        }

        var x = 1
        fab.onClick {
            result = "fib($x) = ${fib(x)}"
            x++
        }
    }

    fun View.onClick(action: suspend (View) -> Unit) {
//        val eventActor = GlobalScope.actor<View>(Dispatchers.Main) {
        val eventActor = GlobalScope.actor<View>(Dispatchers.Main, capacity = Channel.CONFLATED) {
            for (event in channel) action(event)
        }
        setOnClickListener {
            eventActor.offer(it)
        }
    }

    suspend fun fib(x: Int): Int = withContext(Dispatchers.Default) {
        fibBlocking(x)
    }

    fun fibBlocking(x: Int): Int =
        if (x <= 1) x else fibBlocking(x - 1) + fibBlocking(x - 2)
}

