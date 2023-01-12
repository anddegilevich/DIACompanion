package com.almazov.diacompanion.record_info

import FoodInMealInfoAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.*
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.FoodEntity
import com.almazov.diacompanion.meal.FoodInMealItem
import com.almazov.diacompanion.meal.FoodInMealListAdapter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.color.MaterialColors
import kotlinx.android.synthetic.main.fragment_meal_record_info.*
import kotlinx.android.synthetic.main.fragment_meal_record_info.btn_delete
import kotlinx.android.synthetic.main.fragment_meal_record_info.recycler_view_food_in_meal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MealRecordInfo : Fragment(), FoodInMealListAdapter.InterfaceFoodInMeal {
    private val args by navArgs<MealRecordInfoArgs>()
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    var mealInfo = listOf<Double>()
    var foodList = mutableListOf<FoodInMealItem>()
    lateinit var adapter: FoodInMealInfoAdapter
    private var bmi: Double? = null
    private lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        return inflater.inflate(R.layout.fragment_meal_record_info, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("mealTypeKey")?.observe(viewLifecycleOwner) {
                if (it != null) {
                    args.selectedRecord.mainInfo = it
                }
            }

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        appDatabaseViewModel.getMealWithFoods(args.selectedRecord.id).observe(viewLifecycleOwner, Observer{record ->
            if (!record.isNullOrEmpty()) {
                foodList.clear()
                adapter.notifyDataSetChanged()
                for (food in record) {
                    foodList.add(FoodInMealItem(food.food, food.weight!!))
                    adapter.notifyItemInserted(foodList.size)
                }

                setPieChart()
                tv_kkal.text = mealInfo[3].toInt().toString() + " ККал"
                tv_gi.text = mealInfo[4].toInt().toString()
                tv_gl.text = mealInfo[5].toInt().toString()
                tv_weight.text = mealInfo[6].toInt().toString() + " гр."

                val appType = sharedPreferences.getString("APP_TYPE", "")!!
                if ((record[0].meal.sugarLevel != null) and (appType == "GDMRCT")) {
                    slideView(layout_recommendation)
                    slideView(layout_sugar_level)
                    tv_sugar_level_before.text = record[0].meal.sugarLevel.toString()
                    tv_sugar_level_predict.text = record[0].meal.sugarLevelPredicted.toString()

                    GlobalScope.launch(Dispatchers.Main) {
                        val today = args.selectedRecord.date!!
                        val yesterday = getYesterdayDate(today)
                        val todayRecords = GlobalScope.async(Dispatchers.Default) {
                            appDatabaseViewModel.getMealWithFoodsThisDay(today)
                        }
                        val yesterdayRecords = GlobalScope.async(Dispatchers.Default) {
                            appDatabaseViewModel.getMealWithFoodsThisDay(yesterday)
                        }

                        val highGI = checkGI(record)
                        val manyCarbs = checkCarbs(record[0].meal.type!!,record)
                        val highBGBefore = checkSLBefore(record[0].meal.sugarLevel!!)
                        val lowPV = checkPV(record,todayRecords.await(), yesterdayRecords.await())
                        try {
                            val recommendationMessage = getMessage(highGI, manyCarbs, highBGBefore, lowPV, record[0].meal.sugarLevelPredicted!!, resources)
                            tv_recommendation.text = recommendationMessage
                        } catch (e: java.lang.IllegalStateException) {
                        }
                    }
                }
            }
        })


        adapter = FoodInMealInfoAdapter(foodList, this)
        recycler_view_food_in_meal.adapter = adapter
        recycler_view_food_in_meal.layoutManager = LinearLayoutManager(requireContext())

        date.text = args.selectedRecord.date
        time.text = args.selectedRecord.time

        btn_edit.setOnClickListener{
            val action = MealRecordInfoDirections.actionMealRecordInfoToMealAddRecord(args.selectedRecord)
            findNavController().navigate(action)
        }

        btn_delete.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton(this.resources.getString(R.string.Yes)) {_, _ ->
                appDatabaseViewModel.deleteMealRecord(args.selectedRecord.id)
                args.selectedRecord.let { appDatabaseViewModel.deleteRecord(it) }
                findNavController().popBackStack()
            }
            builder.setNegativeButton(this.resources.getString(R.string.No)) {_, _ ->
            }
            builder.setTitle(this.resources.getString(R.string.DeleteRecord))
            builder.setMessage(this.resources.getString(R.string.AreUSureDeleteRecord))
            builder.create().show()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setPieChart() {
        mealInfo = getMealInfo(foodList)
        tv_protein.text = mealInfo[0].toInt().toString() + " гр."
        tv_fats.text = mealInfo[1].toInt().toString() + " гр."
        tv_carbs.text = mealInfo[2].toInt().toString() + " гр."
        val names = listOf("Белки", "Жиры", "Углеводы")
        val pieEntries = ArrayList<PieEntry>()
        for (i in 0..2)
            pieEntries.add(PieEntry(mealInfo[i].toFloat(),names[i]))
        val colorsIds = listOf(R.color.green, R.color.yellow, R.color.orange)
        val darkColorsIds = listOf(R.color.green_dark, R.color.yellow_dark, R.color.orange_dark)
        val pieColors = ArrayList<Int>()
        val darkPieColors = ArrayList<Int>()
        for (i in 0 until colorsIds.count()) {
            pieColors.add(ContextCompat.getColor(requireContext(), colorsIds[i]))
            darkPieColors.add(ContextCompat.getColor(requireContext(), darkColorsIds[i]))
        }
        val pieDataset = PieDataSet(pieEntries,"")
        pieDataset.colors = pieColors
        val pieData = PieData(pieDataset)
        pieData.setDrawValues(true)
        pieData.setValueFormatter(PercentFormatter(meal_pie_chart))
        pieData.setValueTextSize(18f)

        val textColor = MaterialColors.getColor(requireContext(), R.attr.main_text_color, Color.BLACK)
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.transparent)
        pieData.setValueTextColor(textColor)

        meal_pie_chart.apply{
            isDrawHoleEnabled = true
            setUsePercentValues(true)
            centerText = args.selectedRecord.mainInfo
            setCenterTextSize(20f)
            setCenterTextColor(textColor)
            description.isEnabled = false
            legend.isEnabled = false
            setDrawEntryLabels(false)
            isHighlightPerTapEnabled = false
            setHoleColor(backgroundColor)
            data = pieData
            animateY(1200, Easing.EaseInOutQuad)
            invalidate()
        }


    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(),
            R.style.InsulinTheme
        )
        return inflater.cloneInContext(contextThemeWrapper)
    }

    private fun getYesterdayDate(today: String): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val date1 = formatter.parse(today)

        val calendar = Calendar.getInstance()
        calendar.time = date1

        calendar.add(Calendar.DATE, -1)

        return formatter.format(calendar.time)
    }

    override fun updateRecommendationWeight(position: Int, weight: Double) {

    }

}