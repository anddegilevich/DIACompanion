package com.almazov.diacompanion.record_history

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

class RecordListAdapter(): RecyclerView.Adapter<RecordListAdapter.HomeRecordsViewHolder>()  {
    var context: Context? = null
    private var recordsList = emptyList<RecordEntity>()

    var categoriesAndImages = mutableMapOf(
        "sugar_level_table" to R.drawable.sugar_level,
        "insulin_table" to R.drawable.insulin,
        "meal_table" to R.drawable.meal,
        "workout_table" to R.drawable.workout,
        "sleep_table" to R.drawable.sleep,
        "weight_table" to R.drawable.weight,
        "ketone_table" to R.drawable.ketone
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecordsViewHolder {
        context=parent.context;

        return HomeRecordsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.record_row,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeRecordsViewHolder, position: Int) {
        val record = recordsList[position]

        val imageCategory = categoriesAndImages.get(record.category)

        holder.itemView.main_info.text = record.mainInfo

        holder.itemView.time.text = record.time

        holder.itemView.date.text = record.date

        holder.itemView.img_category.setImageResource(imageCategory!!)
        holder.itemView.card_view.setOnClickListener{

            when (record.category) {
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

    fun setData(records: List<RecordEntity>) {
        this.recordsList = records
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    class HomeRecordsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}
}