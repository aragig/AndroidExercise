package com.apppppp.trysavedinstancestate

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

private const val TEXT_FRAGMENT_TAG = "textFragment"

class MainActivity : AppCompatActivity(), ButtonFragment.OnFragmentInteractionListener {

    override fun onButtonClicked() {
        val fragment = supportFragmentManager.findFragmentByTag(TEXT_FRAGMENT_TAG) as TextFragment
        fragment.update()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentByTag(TEXT_FRAGMENT_TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, TextFragment.newInstance(100), TEXT_FRAGMENT_TAG)
                .commit()
        }
    }
}
