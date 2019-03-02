package com.apppppp.tryfragment

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), HogeFragment.OnHogeFragmentListener, FugaFragment.OnFugaFragmentListener {

    override fun onHugaFragmentFinish() {
        supportFragmentManager.popBackStack()
    }


    override fun onHogeFragmentAddFragment() {

        val hugaFragment = FugaFragment.newInstance("りんご", "バナナ")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, hugaFragment)
        transaction.addToBackStack(null) // バックスタックに保存する。呼び出さなければ積まれない。
        transaction.commit()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hogeFragment = HogeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, hogeFragment)
        transaction.commit()
    }
}
