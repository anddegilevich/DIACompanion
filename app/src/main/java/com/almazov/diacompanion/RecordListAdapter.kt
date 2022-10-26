package com.almazov.diacompanion

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.ListFragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.data.RecordEntity
import kotlinx.android.synthetic.main.record_row.view.*

class RecordListAdapter: RecyclerView.Adapter<RecordListAdapter.MyViewHolder>() {

    private var recordList = emptyList<RecordEntity>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.record_row,
            parent, false))
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = recordList[position]
        holder.itemView.main_info.text = currentItem.mainInfo
        holder.itemView.time.text = currentItem.time
        holder.itemView.date.text = currentItem.date
        holder.itemView.card_view.setOnClickListener{
            val action = RecordHistoryDirections.actionRecordHistoryToSugarLevelAddRecord(currentItem)
            holder.itemView.findNavController().navigate(action)

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(record: List<RecordEntity>){
        this.recordList = record
        notifyDataSetChanged()
    }

}