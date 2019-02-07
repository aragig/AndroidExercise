package com.apppppp.trylistview

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextClock
import android.widget.TextView
import java.util.*

class TimeZoneAdapter(private val context: Context,
                      private val timeZones: Array<String> = TimeZone.getAvailableIDs())
    : BaseAdapter() {


    private val inflater = LayoutInflater.from(context)

    // インデックスp0にある行のビューを返す
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: createView(parent)
        val timeZoneId = getItem(position)
        val timeZone = TimeZone.getTimeZone(timeZoneId)

        val viewHolder = view.tag as ViewHolder

        @SuppressLint("SetTextI18n")
        viewHolder.name.text = "${timeZone.displayName}(${timeZone.id})"
        viewHolder.clock.timeZone = timeZone.id

        return view
    }

    // インデックスp0にあるデータを返す
    override fun getItem(position: Int) = timeZones[position]

    // 行を識別するためのユニーク値
    override fun getItemId(position: Int) = position.toLong()

    // リスト表示するデータ件数
    override fun getCount() = timeZones.size


    private var count = 0

    private fun createView(parent: ViewGroup?) : View {
        println(count++)

        val view = inflater.inflate(R.layout.list_time_zone_row, parent, false)
        view.tag = ViewHolder(view)
        return view
    }


    private class ViewHolder(view: View) {
        val name = view.findViewById<TextView>(R.id.timeZone)
        val clock = view.findViewById<TextClock>(R.id.clock)
    }

}