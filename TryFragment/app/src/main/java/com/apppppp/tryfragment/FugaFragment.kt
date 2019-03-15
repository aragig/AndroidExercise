package com.apppppp.tryfragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FugaFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFugaFragmentListener? = null
    private var fugaText:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fuga, container, false)

        fugaText = view.findViewById(R.id.fugaText)


        val displayParams = view.findViewById<Button>(R.id.displayParams)
        displayParams.setOnClickListener {
            displayParams()
        }

        val finishButton = view.findViewById<Button>(R.id.finishButton)
        finishButton.setOnClickListener {
            finish()
        }
        return view
    }

    fun displayParams() {
        fugaText?.text = "$ARG_PARAM1: $param1 / $ARG_PARAM2: $param2"
    }

    fun finish() {
        listener?.onHugaFragmentFinish()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFugaFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFugaFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        println("FugaFragment onDetach")
    }


    interface OnFugaFragmentListener {
        fun onHugaFragmentFinish()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FugaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
