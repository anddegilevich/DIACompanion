package com.almazov.diacompanion.home

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.food_in_meal_row.view.*

class HomeRecordsAdapter(): RecyclerView.Adapter<HomeRecordsAdapter.HomeRecordsViewHolder>()  {
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecordsViewHolder {
        context=parent.context;

        return HomeRecordsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.record_card,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeRecordsViewHolder, position: Int) {
        /*var foodItem = foodItemList[position]*/

    }

    override fun getItemCount(): Int {
        return 1
    }

    class HomeRecordsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}
}