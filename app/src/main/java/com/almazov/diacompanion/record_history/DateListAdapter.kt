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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DateListAdapter(
    val appDatabaseViewModel: AppDatabaseViewModel,
    val viewLifecycleOwner: LifecycleOwner
) : PagingDataAdapter<DateClass, DateListAdapter.RecordViewHolder>(DIFF_CALLBACK) {

    var context: Context? = null
    var filter = ""
    val fullDaysChanges = mutableListOf<Pair<String?, Boolean>>()
    private val dateChanges = mutableListOf<String?>()

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


        var second = false
        if (dateChanges.contains(date?.date))
        {
            var i = dateChanges.count()
            while (i>0){
                if (fullDaysChanges[i-1].first == date?.date) {
                    second = fullDaysChanges[i-1].second
                    break
                }
                i -= 1
            }
        } else second = date?.fullDay!!

        holder.itemView.date.isChecked = second
        holder.itemView.date.setOnClickListener{
            fullDaysChanges.add(Pair(date?.date,
                holder.itemView.date.isChecked))
            dateChanges.add(date?.date)
        }

        val recyclerView = holder.itemView.recycler_view_date_records
        val adapter = RecordListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        if (filter.isBlank()){
            appDatabaseViewModel.readDayRecords(date?.date).observe(viewLifecycleOwner , Observer { records ->
                adapter.setData(records)
            })
        } else {
            appDatabaseViewModel.readDayRecords(date?.date,filter).observe(viewLifecycleOwner , Observer { records ->
                adapter.setData(records)
            })
        }


    }

}