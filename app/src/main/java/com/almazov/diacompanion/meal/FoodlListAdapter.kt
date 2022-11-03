package com.almazov.diacompanion.meal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.FoodEntity
import kotlinx.android.synthetic.main.food_row.view.*

class FoodlListAdapter(): PagingDataAdapter<FoodEntity, FoodlListAdapter.FoodViewHolder>(DIFF_CALLBACK) {
    var context: Context? = null

    class FoodViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}


    companion object {
        private val DIFF_CALLBACK = object :
        DiffUtil.ItemCallback<FoodEntity>() {

            override fun areItemsTheSame(oldItem: FoodEntity, newItem: FoodEntity): Boolean {
                return oldItem.idFood == newItem.idFood
            }

            override fun areContentsTheSame(oldItem: FoodEntity, newItem: FoodEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        context=parent.context;

        return FoodViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.food_row,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food: FoodEntity? = getItem(position)
        holder.itemView.tv_food_name.text = food?.name
    }



}