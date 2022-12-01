package com.almazov.diacompanion.base
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class CustomStringAdapter(context: Context, resource: Int, dates: Array<String>) :
    ArrayAdapter<String>(context, resource, dates) {
    private val itemsToHide = mutableListOf<Int>()
    
    fun setItemsToHide(items: List<Int>) {
        itemsToHide.clear()
        for (item in items){
            itemsToHide.add(item)
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View
        if (position in itemsToHide) {
            val tv = TextView(context)
            tv.visibility = View.GONE
            tv.height = 0
            v = tv
            v.setVisibility(View.GONE)
        } else v = super.getDropDownView(position, null, parent)
        return v
    }
}