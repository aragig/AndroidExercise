package com.apppppp.trysavedinstancestate

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


private const val COUNTER_KEY = "counter"

class TextFragment : Fragment() {

    private var mCounter = 0
    private lateinit var counterTextView: TextView

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(COUNTER_KEY, mCounter)
        super.onSaveInstanceState(outState)
    }

    fun update() {
        mCounter++
        counterTextView.text = mCounter.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCounter = savedInstanceState?.getInt(COUNTER_KEY)
                    ?: arguments?.getInt(COUNTER_KEY)
                    ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_text, container, false)
        counterTextView = view.findViewById(R.id.textView)
        counterTextView.text = mCounter.toString()
        return view
    }



    companion object {
        @JvmStatic
        fun newInstance(counter: Int) =
            TextFragment().apply {
                arguments = Bundle().apply {
                    putInt(COUNTER_KEY, counter)
                }
            }
    }
}
