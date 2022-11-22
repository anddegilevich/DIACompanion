package com.almazov.diacompanion.meal

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.food_in_meal_row.view.*

class FoodInMealListAdapter(private val foodItemList:MutableList<FoodInMealItem>, private val mListener: InterfaceFoodInMeal)
    : RecyclerView.Adapter<FoodInMealListAdapter.FoodInMealItemViewHolder>() {


    var context: Context? = null
    val minWeight = 0
    val maxWeight = 500

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodInMealItemViewHolder {
        context=parent.context

        return FoodInMealItemViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.food_in_meal_row,
                parent, false
            ), mListener
        )
    }


    override fun onBindViewHolder(holder: FoodInMealItemViewHolder, position: Int) {
        var foodItem = foodItemList[position]
        holder.itemView.tv_food_in_meal_name.text = foodItem.foodEntity.name
        val editText = holder.itemView.edit_text_food_in_meal_weight
        editText.setText(foodItem.weight.toString())
        val intColor = if (foodItem.foodEntity.gi!! > 55) R.color.red
        else if (foodItem.foodEntity.gi!! > 25) R.color.orange
        else R.color.green
        val itemColor = ContextCompat.getColor(context!!,intColor)
        /*holder.itemView.tv_food_in_meal_name.setTextColor(itemColor)
        holder.itemView.edit_text_food_in_meal_weight.setTextColor(itemColor)
        holder.itemView.tv_weight_units.setTextColor(itemColor)*/
        holder.itemView.gi_indexer.setBackgroundColor(itemColor)

        /*editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val string = editText.text.toString()
                if (string != "") {
                    if (string.toBigDecimal().toInt() > maxWeight) {
                        editText.setText(maxWeight.toString())
                        editText.setSelection(editText.length())
                    }else if (string.toBigDecimal().toInt() < minWeight) {
                        editText.setText(minWeight.toString())
                        editText.setSelection(editText.length())
                    }
                    foodItem.weight = editText.text.toString().toDouble()
                }
            }
        }*/
        editText.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(string: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (string.toString() != "") {
                    if (string.toString().toBigDecimal().toInt() > maxWeight) {
                        editText.setText(maxWeight.toString())
                        editText.setSelection(editText.length())
                    }

                    if (string.toString().toBigDecimal().toInt() < minWeight) {
                        editText.setText(minWeight.toString())
                        editText.setSelection(editText.length())
                    }
                    holder.mListener.updateRecommendationWeight(position,editText.text.toString().toDouble())
                }
            }

            override fun afterTextChanged(string: Editable) {

            }

            override fun beforeTextChanged(string: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

        })
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    class FoodInMealItemViewHolder(itemView: View, val mListener: InterfaceFoodInMeal): RecyclerView.ViewHolder(itemView)

    interface InterfaceFoodInMeal{
        fun updateRecommendationWeight(position: Int, weight: Double)
    }
}