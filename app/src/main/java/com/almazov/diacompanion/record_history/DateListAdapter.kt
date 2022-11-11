package com.almazov.diacompanion.record_history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.DateClass
import kotlinx.android.synthetic.main.record_date_section.view.*

class DateListAdapter(
    val appDatabaseViewModel: AppDatabaseViewModel,
    val viewLifecycleOwner: LifecycleOwner
) : PagingDataAdapter<DateClass, DateListAdapter.RecordViewHolder>(DIFF_CALLBACK) {

    var context: Context? = null
    var filter = ""

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<DateClass>() {

            override fun areItemsTheSame(oldItem: DateClass, newItem: DateClass): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: DateClass, newItem: DateClass): Boolean {
                return oldItem.date != newItem.date
            }
        }
    }


    class RecordViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        context=parent.context;

        return RecordViewHolder(
            LayoutInflater.from(context).inflate(
            R.layout.record_date_section,
            parent, false))
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {

        val date: DateClass? = getItem(position)

        holder.itemView.date.text = date?.date

        val recyclerView = holder.itemView.recycler_view_date_records
        val adapter = RecordListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        if (filter.isBlank()){
            appDatabaseViewModel.readDayRecords(date?.date!!).observe(viewLifecycleOwner , Observer { records ->
                adapter.setData(records)
            })
        } else {
            appDatabaseViewModel.readDayRecords(date?.date!!,filter).observe(viewLifecycleOwner , Observer { records ->
                adapter.setData(records)
            })
        }


    }


}