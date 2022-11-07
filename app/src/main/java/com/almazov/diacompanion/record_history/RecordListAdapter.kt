package com.almazov.diacompanion.record_history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.RecordEntity
import kotlinx.android.synthetic.main.record_row.view.*

class RecordListAdapter(): PagingDataAdapter<RecordEntity, RecordListAdapter.RecordViewHolder>(DIFF_CALLBACK) {

    var context: Context? = null

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<RecordEntity>() {

            override fun areItemsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

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

    class RecordViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        context=parent.context;

        return RecordViewHolder(LayoutInflater.from(context).inflate(
            R.layout.record_row,
            parent, false))
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {

        val record: RecordEntity? = getItem(position)

        val primaryColor = categoriesAndPrimaryColors.get(record?.category)
        val secondaryColor = categoriesAndSecondaryColors.get(record?.category)
        val imageCategory = categoriesAndImages.get(record?.category)

        holder.itemView.main_info.text = record?.mainInfo
        holder.itemView.main_info.setTextColor(ContextCompat.getColor(context!!, secondaryColor!!))

        holder.itemView.time.text = record?.time
        holder.itemView.time.setTextColor(ContextCompat.getColor(context!!, secondaryColor!!))

        holder.itemView.date.text = record?.date
        holder.itemView.date.setTextColor(ContextCompat.getColor(context!!, secondaryColor!!))

        holder.itemView.img_category.setImageResource(imageCategory!!)

        holder.itemView.card_view.setBackgroundResource(primaryColor!!)
        holder.itemView.card_view.setOnClickListener{

            when (record?.category) {
                "sugar_level_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToSugarLevelAddRecord(record)
                    holder.itemView.findNavController().navigate(action)}

                "insulin_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToInsulinAddRecord(record)
                    holder.itemView.findNavController().navigate(action)}

                "meal_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToMealAddRecord(record)
                    holder.itemView.findNavController().navigate(action)}

                "workout_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToWorkoutAddRecord(record)
                    holder.itemView.findNavController().navigate(action)}

                "sleep_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToSleepAddRecord(record)
                    holder.itemView.findNavController().navigate(action)}

                "weight_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToWeightAddRecord(record)
                    holder.itemView.findNavController().navigate(action)}

                "ketone_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToKetoneAddRecord(record)
                    holder.itemView.findNavController().navigate(action)}
            }
        }

    }


}