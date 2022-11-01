package com.almazov.diacompanion.record_history

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.RecordEntity
import kotlinx.android.synthetic.main.record_row.view.*

class RecordListAdapter(): RecyclerView.Adapter<RecordListAdapter.MyViewHolder>() {

    private var recordList = emptyList<RecordEntity>()
    var context: Context? = null

    var categoriesAndPrimaryColors = mutableMapOf(
        "sugar_level_table" to R.color.red,
        "insulin_table" to R.color.blue,
        "meal_table" to R.color.purple,
        "workout_table" to R.color.green,
        "sleep_table" to R.color.pink,
        "weight_table" to R.color.yellow,
        "ketone_table" to R.color.orange
    )

    var categoriesAndSecondaryColors = mutableMapOf(
        "sugar_level_table" to R.color.red_dark,
        "insulin_table" to R.color.blue_dark,
        "meal_table" to R.color.purple_dark,
        "workout_table" to R.color.green_dark,
        "sleep_table" to R.color.pink_dark,
        "weight_table" to R.color.yellow_dark,
        "ketone_table" to R.color.orange_dark
    )

    var categoriesAndImages = mutableMapOf(
        "sugar_level_table" to R.drawable.sugar_level,
        "insulin_table" to R.drawable.insulin,
        "meal_table" to R.drawable.meal,
        "workout_table" to R.drawable.workout,
        "sleep_table" to R.drawable.sleep,
        "weight_table" to R.drawable.weight,
        "ketone_table" to R.drawable.ketone
    )

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context=parent.context;

        return MyViewHolder(LayoutInflater.from(context).inflate(
            R.layout.record_row,
            parent, false))
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = recordList[position]

        val primaryColor = categoriesAndPrimaryColors.get(currentItem.category)
        val secondaryColor = categoriesAndSecondaryColors.get(currentItem.category)
        val imageCategory = categoriesAndImages.get(currentItem.category)

        holder.itemView.main_info.text = currentItem.mainInfo
//        holder.itemView.main_info.setTextColor(ContextCompat.getColor(context!!, secondaryColor!!))

        holder.itemView.time.text = currentItem.time
        holder.itemView.time.setTextColor(ContextCompat.getColor(context!!, secondaryColor!!))

        holder.itemView.date.text = currentItem.date
        holder.itemView.date.setTextColor(ContextCompat.getColor(context!!, secondaryColor!!))

        holder.itemView.img_category.setImageResource(imageCategory!!)

        holder.itemView.card_view.setBackgroundResource(primaryColor!!)
        holder.itemView.card_view.setOnClickListener{

            when (currentItem.category) {
                "sugar_level_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToSugarLevelAddRecord(currentItem)
                    holder.itemView.findNavController().navigate(action)}

                "insulin_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToInsulinAddRecord(currentItem)
                    holder.itemView.findNavController().navigate(action)}

                "meal_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToMealAddRecord(currentItem)
                    holder.itemView.findNavController().navigate(action)}

                "workout_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToWorkoutAddRecord(currentItem)
                    holder.itemView.findNavController().navigate(action)}

                "sleep_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToSleepAddRecord(currentItem)
                    holder.itemView.findNavController().navigate(action)}

                "weight_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToWeightAddRecord(currentItem)
                    holder.itemView.findNavController().navigate(action)}

                "ketone_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToKetoneAddRecord(currentItem)
                    holder.itemView.findNavController().navigate(action)}
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(records: List<RecordEntity>){
        this.recordList = records
        notifyDataSetChanged()
    }

}