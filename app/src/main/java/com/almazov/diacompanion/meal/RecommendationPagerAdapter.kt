package com.almazov.diacompanion.meal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.recommendation_block.view.*

class RecommendationPagerAdapter(var recommendations: List<String>) : RecyclerView.Adapter<RecommendationPagerAdapter.PagerViewHolder>() {

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendationPagerAdapter.PagerViewHolder {
        return PagerViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.recommendation_block, parent, false))
    }

    override fun onBindViewHolder(
        holder: RecommendationPagerAdapter.PagerViewHolder,
        position: Int
    ) {
        holder.itemView.tv_recommendation.text = recommendations[position]
    }

    override fun getItemCount(): Int {
        return recommendations.size
    }

    fun updateItems(updatedItems: List<String>) {
        recommendations = updatedItems
        notifyDataSetChanged()
    }
}