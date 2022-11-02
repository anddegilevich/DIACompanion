package com.almazov.diacompanion.meal

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
import com.almazov.diacompanion.record_history.RecordHistoryDirections
import kotlinx.android.synthetic.main.meal_row.view.*
import kotlinx.android.synthetic.main.record_row.view.*

class MealListAdapter(): PagingDataAdapter<RecordEntity, MealListAdapter.MealViewHolder>(DIFF_CALLBACK) {
    var context: Context? = null

    class MealViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}


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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        context=parent.context;

        return MealViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.record_row,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal: RecordEntity? = getItem(position)
//        holder.itemView.tv_meal_name.text = meal?.mainInfo

        holder.itemView.main_info.text = meal?.mainInfo

        holder.itemView.time.text = meal?.time

        holder.itemView.date.text = meal?.date

        holder.itemView.img_category.setImageResource(R.drawable.sleep)
        holder.itemView.card_view.setOnClickListener{

            when (meal?.category) {
                "sugar_level_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToSugarLevelAddRecord(meal)
                    holder.itemView.findNavController().navigate(action)}

                "insulin_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToInsulinAddRecord(meal)
                    holder.itemView.findNavController().navigate(action)}

                "meal_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToMealAddRecord(meal)
                    holder.itemView.findNavController().navigate(action)}

                "workout_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToWorkoutAddRecord(meal)
                    holder.itemView.findNavController().navigate(action)}

                "sleep_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToSleepAddRecord(meal)
                    holder.itemView.findNavController().navigate(action)}

                "weight_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToWeightAddRecord(meal)
                    holder.itemView.findNavController().navigate(action)}

                "ketone_table" -> {val action = RecordHistoryDirections.actionRecordHistoryToKetoneAddRecord(meal)
                    holder.itemView.findNavController().navigate(action)}
            }
        }
    }



}