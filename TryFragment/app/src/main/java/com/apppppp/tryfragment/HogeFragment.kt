package com.apppppp.tryfragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class HogeFragment : Fragment() {

    private var listener: OnHogeFragmentListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("HogeFragmentのonCreateViewが呼ばれた")
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_hoge, container, false)


        val addFragment = fragmentView.findViewById<Button>(R.id.addFragment)
        addFragment.setOnClickListener {
            addFragment()
        }

        return fragmentView
    }



    fun addFragment() {
        listener?.onHogeFragmentAddFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHogeFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnHogeFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnHogeFragmentListener {
        fun onHogeFragmentAddFragment()
    }


}
