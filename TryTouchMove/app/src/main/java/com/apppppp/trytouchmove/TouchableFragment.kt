package com.apppppp.trytouchmove

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TouchableFragment : Fragment(),View.OnTouchListener {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnTouchableFragmentListener? = null

    private var yPrec = 0.0f

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                yPrec = event.getY(0)
                listener?.actionDown()
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = yPrec - event.getY(0)
                listener?.actionMove(dy)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                listener?.actionUp()
            }
        }
        return true
    }

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

        val view = inflater.inflate(R.layout.fragment_touchable, container, false)
        view.findViewById<View>(R.id.touchableView).setOnTouchListener(this)
        return view
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTouchableFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnTouchableFragmentListener {
        fun actionDown()
        fun actionMove(dy: Float)
        fun actionUp()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TouchableFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
