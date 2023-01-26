package com.almazov.diacompanion.meal

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.slideView
import kotlinx.android.synthetic.main.food_in_meal_row.view.*

open class FoodInMealListAdapter(private val mListener: InterfaceFoodInMeal)
    : RecyclerView.Adapter<FoodInMealListAdapter.FoodInMealItemViewHolder>() {

    var foodItemList = mutableListOf<FoodInMealItem>()
    var context: Context? = null
    var glList = mutableListOf<Double>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodInMealItemViewHolder {
        context=parent.context

        return FoodInMealItemViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.food_in_meal_row,
                parent, false
            ), mListener
        )
    }


    override fun onBindViewHolder(holder: FoodInMealItemViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val foodItem = foodItemList[position]
        val gl = glList[position]
        holder.itemView.tv_food_in_meal_name.text = foodItem.foodEntity.name
        val editText = holder.itemView.tv_food_in_meal_weight
        editText.setText(foodItem.weight.toString())

        val intColor = if (foodItem.foodEntity.gi != null ) {
            if (foodItem.foodEntity.gi > 55) R.color.red
            else if (foodItem.foodEntity.gi > 25) R.color.orange
            else R.color.green
        } else R.color.green
        val itemColor = ContextCompat.getColor(context!!,intColor)

        holder.itemView.setOnClickListener {
            slideView(holder.itemView.recipe_layout)
        }

//        holder.itemView.gi_indexer.setBackgroundColor(itemColor)
        holder.itemView.gi.text = if (foodItem.foodEntity.gi != null)
            foodItem.foodEntity.gi.toString() else "0"
        holder.itemView.gi.setTextColor(itemColor)
        holder.itemView.gl.text = gl.toInt().toString()
        holder.itemView.tv_food_in_meal_weight.text = foodItem.weight.toString()

        changeWeight(holder, position)
    }

    open fun changeWeight(holder: FoodInMealItemViewHolder, position: Int) {
        val editText = holder.itemView.tv_food_in_meal_weight
        editText.setOnClickListener {
            holder.mListener.updateRecommendationWeight(position)
        }
    }

    private fun calculateGL(){
        glList.clear()
        for (food in foodItemList){

            val gi = food.foodEntity.gi ?: 0.0
            val carb = food.foodEntity.carbo ?: 0.0
            val gl = gi * carb * food.weight / 100
            glList.add(gl)
        }
        val glSum = glList.sum()
        for (i in 0 until glList.size){
            glList[i] = glList[i] / glSum * 100
        }
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    fun updateItems(updatedItems: MutableList<FoodInMealItem>) {
        foodItemList = updatedItems
        calculateGL()
        notifyDataSetChanged()
    }

    class FoodInMealItemViewHolder(itemView: View, val mListener: InterfaceFoodInMeal): RecyclerView.ViewHolder(itemView)

    interface InterfaceFoodInMeal{
        fun updateRecommendationWeight(position: Int)
    }
}