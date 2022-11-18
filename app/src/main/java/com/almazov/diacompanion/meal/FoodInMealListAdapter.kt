package com.almazov.diacompanion.meal

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.food_in_meal_row.view.*

class FoodInMealListAdapter(private val foodItemList:MutableList<FoodInMealItem>)
    : RecyclerView.Adapter<FoodInMealListAdapter.FoodInMealItemViewHolder>() {

    var context: Context? = null
    val minWeight = 0
    val maxWeight = 500

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodInMealItemViewHolder {
        context=parent.context;

        return FoodInMealItemViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.food_in_meal_row,
                parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: FoodInMealItemViewHolder, position: Int) {
        var foodItem = foodItemList[position]
        holder.itemView.tv_food_in_meal_name.text = foodItem.foodEntity.name
        val editText = holder.itemView.edit_text_food_in_meal_weight
        editText.setText(foodItem.weight.toString())
        if (foodItem.foodEntity.gi!! > 55) holder.itemView.food_in_meal_row.setBackgroundResource(R.color.red)

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
                    foodItem.weight = editText.text.toString().toDouble()
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

    class FoodInMealItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}