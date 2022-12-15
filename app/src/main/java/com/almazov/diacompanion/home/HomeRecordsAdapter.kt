package com.almazov.diacompanion.home

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.RecordEntity
import kotlinx.android.synthetic.main.fragment_record_history.view.*
import kotlinx.android.synthetic.main.record_card.view.*
import kotlinx.android.synthetic.main.record_card.view.card_view
import kotlinx.android.synthetic.main.record_row.view.*
import kotlinx.android.synthetic.main.record_row.view.date
import kotlinx.android.synthetic.main.record_row.view.img_category
import kotlinx.android.synthetic.main.record_row.view.main_info
import kotlinx.android.synthetic.main.record_row.view.time

class HomeRecordsAdapter(private val mListener: InterfaceRecordsInfo): RecyclerView.Adapter<HomeRecordsAdapter.HomeRecordsViewHolder>()  {
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
                R.layout.record_card,
                parent, false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: HomeRecordsViewHolder, position: Int) {
        val record = recordsList[position]
        val id = record.id

        val imageCategory = categoriesAndImages.get(record.category)

        holder.itemView.main_info.text = record.mainInfo

        holder.itemView.time.text = record.time

        holder.itemView.date.text = record.date

        holder.itemView.img_category.setImageResource(imageCategory!!)

        holder.itemView.card_view.transitionName = "$id record_card"
        holder.itemView.img_category.transitionName = "$id img_category"

        holder.itemView.card_view.setOnClickListener{
            holder.mListener.transitionToRecordInfo(holder.itemView, record)
        }

    }

    fun setData(records: List<RecordEntity>) {
        this.recordsList = records
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    class HomeRecordsViewHolder(itemView: View, val mListener: InterfaceRecordsInfo): RecyclerView.ViewHolder(itemView) {}
}