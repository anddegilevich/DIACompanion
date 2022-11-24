package com.almazov.diacompanion.record_info

import FoodInMealInfoAdapter
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.getProteinFatCarb
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.meal.FoodInMealItem
import com.almazov.diacompanion.meal.FoodInMealListAdapter
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.android.synthetic.main.fragment_meal_record_info.*

class MealRecordInfo : Fragment(), FoodInMealListAdapter.InterfaceFoodInMeal {
    private val args by navArgs<MealRecordInfoArgs>()
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    var foodList = mutableListOf<FoodInMealItem>()
    lateinit var adapter: FoodInMealListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sharedPreferences = context?.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        /*bmi = sharedPreferences!!.getFloat("BMI", 20f).toDouble()*/

        return inflater.inflate(R.layout.fragment_meal_record_info, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        appDatabaseViewModel.getMealWithFoods(args.selectedRecord?.id).observe(viewLifecycleOwner, Observer{record ->

            if (foodList.isNullOrEmpty()) {
                for (food in record) {
                    foodList.add(FoodInMealItem(food.food, food.weight!!))
                    adapter.notifyItemInserted(foodList.size)
                }
            }
            setPieChart()
        })
        adapter = FoodInMealInfoAdapter(foodList, this)
        recycler_view_food_in_meal.adapter = adapter
        recycler_view_food_in_meal.layoutManager = LinearLayoutManager(requireContext())

        date.text = args.selectedRecord.date
        time.text = args.selectedRecord.time
        super.onViewCreated(view, savedInstanceState)
    }

    fun setPieChart() {
        val proteinFatCarb = getProteinFatCarb(foodList)
        val names = listOf("Белки", "Жиры", "Углеводы")
        val pieEntries = ArrayList<PieEntry>()
        for (i in 0 until proteinFatCarb.count())
            pieEntries.add(PieEntry(proteinFatCarb[i].toFloat(),names[i]))
        val colorsIds = listOf(R.color.green, R.color.yellow, R.color.orange)
        val pieColors = ArrayList<Int>()
        for (color in colorsIds)
            pieColors.add(ContextCompat.getColor(requireContext(), color))
        val pieDataset = PieDataSet(pieEntries,"")
        pieDataset.colors = pieColors
        val pieData = PieData(pieDataset)
        pieData.setDrawValues(true)
        pieData.setValueFormatter(PercentFormatter(meal_pie_chart))
        pieData.setValueTextSize(18f)
        val textColor = ContextCompat.getColor(requireContext(), R.color.purple_dark)
        pieData.setValueTextColor(textColor)

        meal_pie_chart.isDrawHoleEnabled = true
        meal_pie_chart.setUsePercentValues(true)
        meal_pie_chart.setEntryLabelColor(textColor)
        meal_pie_chart.centerText = args.selectedRecord.mainInfo
        meal_pie_chart.setCenterTextSize(20f)
        meal_pie_chart.setCenterTextColor(textColor)
        meal_pie_chart.description.isEnabled = false
        meal_pie_chart.legend.textSize = 18f
        meal_pie_chart.legend.textColor = textColor
        meal_pie_chart.setDrawEntryLabels(false)

        meal_pie_chart.data = pieData
        meal_pie_chart.invalidate()
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.MealTheme)
        return inflater.cloneInContext(contextThemeWrapper)
    }

    override fun updateRecommendationWeight(position: Int, weight: Double) {

    }

}