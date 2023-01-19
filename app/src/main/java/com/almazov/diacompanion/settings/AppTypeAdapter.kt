package com.almazov.diacompanion.meal

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.settings.AppType
import kotlinx.android.synthetic.main.app_type_row.view.*

open class AppTypeAdapter(private val mListener: InterfaceAppType, private val appTypes: List<AppType>)
    : RecyclerView.Adapter<AppTypeAdapter.AppTypeViewHolder>() {

    var context: Context? = null
    lateinit var currentAppType: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppTypeViewHolder {
        context=parent.context

        return AppTypeViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.app_type_row,
                parent, false
            ), mListener
        )
    }


    override fun onBindViewHolder(holder: AppTypeViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val appTypeItem = appTypes[position]
        holder.itemView.btn_app_type.setText(appTypeItem.title)
        holder.itemView.btn_app_type.setOnClickListener {
            holder.mListener.selectAppType(position)
        }

        val intColor = if (appTypeItem.name == currentAppType) {
            holder.mListener.getCurrentAppTypePosition(position)
            R.color.blue
        } else R.color.red
        val itemColor = ContextCompat.getColor(context!!,intColor)
        holder.itemView.btn_app_type.setBackgroundColor(itemColor)

    }

    override fun getItemCount(): Int {
        return appTypes.size
    }

    fun updateItems(currentAppType: String) {
        this.currentAppType = currentAppType
        notifyDataSetChanged()
    }

    class AppTypeViewHolder(itemView: View, val mListener: InterfaceAppType): RecyclerView.ViewHolder(itemView)

    interface InterfaceAppType{
        fun selectAppType(position: Int)
        fun getCurrentAppTypePosition(position: Int)
    }
}