package com.almazov.diacompanion.add_record

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.*
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.FoodEntity
import com.almazov.diacompanion.data.MealEntity
import com.almazov.diacompanion.data.RecordEntity
import com.almazov.diacompanion.meal.*
import kotlinx.android.synthetic.main.fragment_meal_add_record.*
import kotlinx.android.synthetic.main.fragment_meal_add_record.btn_delete
import kotlinx.android.synthetic.main.fragment_meal_add_record.btn_save
import kotlinx.android.synthetic.main.fragment_meal_add_record.tv_Date
import kotlinx.android.synthetic.main.fragment_meal_add_record.tv_Time
import kotlinx.android.synthetic.main.fragment_meal_add_record.tv_title
import kotlinx.android.synthetic.main.fragment_meal_add_record.view.*
import kotlinx.android.synthetic.main.sl_high.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter


class MealAddRecord : Fragment(), FoodInMealListAdapter.InterfaceFoodInMeal {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var dateSubmit: Long? = null
    private val args by navArgs<MealAddRecordArgs>()

    var foodList = mutableListOf<FoodInMealItem>()
    var lastFood: Int = 0
    lateinit var adapter: FoodInMealListAdapter
    var updateBool: Boolean = false
    var updateFinished: Boolean = false

    private var bmi: Float = 0f
    private var weightBeforePregnancy: Float = 0f
    private var sixHoursPredictors: SixHoursPredictors = SixHoursPredictors()
    private var pv12: Float = 0f
    private var iterablePredictors: Pair<IterablePredictors, Boolean> =
        Pair(IterablePredictors(), false)
    private var age: Float = 27f


    private var hbA1C = 0f
    private var tg = 0f
    private var hol = 0f
    private var glucoseNt = 0f
    private var analysisTime = 0f
    private var hyperglycemiaChance: Double? = null

    private lateinit var spinnerAdapter: CustomStringAdapter
    private lateinit var spinnerStringArray: Array<String>
    private lateinit var appType: String

    private var recommendations = emptyList<String>()
    private lateinit var recommendationsAdapter: RecommendationPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        bmi = sharedPreferences!!.getFloat("BMI", 20f)
        weightBeforePregnancy = sharedPreferences.getFloat("WEIGHT_BEFORE_PREGNANCY", 60f)
        appType = sharedPreferences.getString("APP_TYPE", "")!!
        val birthDate = sharedPreferences.getString("BIRTH_DATE", "01.01.2000")!!
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val dateTimeBirth = LocalDate.parse(birthDate, formatter)
        age = Period.between(
            dateTimeBirth,
            LocalDate.now()
        ).years.toFloat()

        CoroutineScope(Dispatchers.Main).launch {
            val questionnaire = CoroutineScope(Dispatchers.IO).async {
                appDatabaseViewModel.getQuestionnaire()
            }
            questionnaire.await().let {
                hbA1C = it.hba1c ?: 0f
                tg = it.triglyceride ?: 0f
                hol = it.cholesterol ?: 0f
                glucoseNt = it.glucose ?: 0f
                analysisTime = it.pregnancyAnalysesCount?.toFloat() ?: 0f
            }
        }

        val view = inflater.inflate(R.layout.fragment_meal_add_record, container, false)

        if (appType != "PCOS") {
            slideView(view.layout_pcos)
        }

        updateBool = args.selectedRecord != null
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        recommendationsAdapter = RecommendationPagerAdapter(emptyList())

        pager_recommendations.adapter = recommendationsAdapter
        pager_recommendations.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        pager_recommendations_indicator.setViewPager(pager_recommendations)


        spinnerStringArray = resources.getStringArray(R.array.MealSpinner)
        spinnerAdapter = CustomStringAdapter(
            requireContext(),
            R.layout.spinner_item, spinnerStringArray
        )

        spinner_meal.adapter = spinnerAdapter
        spinner_meal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (!foodList.isNullOrEmpty() and checkbox_sugar_level.isChecked
                    and !edit_text_sugar_level.text.isNullOrEmpty()
                ) updateRecommendation()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        }

        tv_Date.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                setupSpinnerPreferences(s.toString())
            }
        })

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val time = tv_Time.text.toString()
                val date = tv_Date.text.toString()
                val dateInMilli = convertDateToMils("$time $date")
                getTimeIntervalPredictors(dateInMilli)
            }
        }
        dateSubmit = timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        tv_Time.addTextChangedListener(textWatcher)
        tv_Date.addTextChangedListener(textWatcher)
        val recyclerView = view.recycler_view_food_in_meal
        adapter = FoodInMealListAdapter(this)

        val swipeDeleteFood = object : SwipeDeleteFood(requireContext(), R.color.blue_dark) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        foodList.removeAt(viewHolder.adapterPosition)
                        adapter.updateItems(foodList)
                        if (foodList.isNullOrEmpty() and (vf_recommendation.height != 0)) {
                            slideView(vf_recommendation)
                        } else
                            if (checkbox_sugar_level.isChecked
                                and !edit_text_sugar_level.text.isNullOrEmpty()
                            ) {
                                iterablePredictors = getGLCarbsKr(foodList)
                                updateRecommendation()
                            }
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeDeleteFood)
        touchHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.updateItems(foodList)

        Navigation.findNavController(view).currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FoodEntity>("foodKey")?.observe(viewLifecycleOwner) {

                if (it != null) {

                    var foodAlreadyInList = false
                    for (food in foodList) {
                        if (it.name == food.foodEntity.name) foodAlreadyInList = true
                    }

                    if (!foodAlreadyInList) {
                        lastFood = it.idFood!!
                        val selectWeightDialog = SelectWeightDialog(requireContext(), null)
                        selectWeightDialog.isCancelable = true
                        selectWeightDialog.show(requireFragmentManager(), "weight select dialog")

                        setFragmentResultListener("requestKey") { _, bundle ->
                            val result = bundle.getString("resultKey")
                            foodList.add(FoodInMealItem(it, result!!.toDouble()))
                            adapter.updateItems(foodList)
                            iterablePredictors = getGLCarbsKr(foodList)
                            if (checkbox_sugar_level.isChecked and
                                !edit_text_sugar_level.text.isNullOrEmpty()
                            ) {
                                updateRecommendation()
                            }
                        }
                    }
                } else {
                    if (!foodList.isNullOrEmpty() and checkbox_sugar_level.isChecked and !edit_text_sugar_level.text.isNullOrEmpty()) {
                        updateRecommendation()
                    }
                }
            }
        btn_add_food.setOnClickListener {
            if (vf_recommendation.height != 0) slideView(vf_recommendation)
            Navigation.findNavController(view).navigate(R.id.action_mealAddRecord_to_foodList)
        }

        btn_save.setOnClickListener {
            val sugarLevelSubmit = checkbox_sugar_level.isChecked
            val mealIsEmpty = foodList.isNullOrEmpty()
            val finalBool = if (sugarLevelSubmit) {
                val sugarLevelNotEntered = edit_text_sugar_level.text.isNullOrEmpty()
                !sugarLevelNotEntered and !mealIsEmpty
            } else {
                !mealIsEmpty
            }
            if (finalBool) {
                if (updateBool) {
                    updateRecord(sugarLevelSubmit)
                } else {
                    addRecord(sugarLevelSubmit)
                }
            }
        }

        checkbox_sugar_level.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                slideView(sugar_level_layout)
                if (!foodList.isNullOrEmpty() and !edit_text_sugar_level.text.isNullOrEmpty()) updateRecommendation()
            } else {
                if (vf_recommendation.height != 0) slideView(vf_recommendation)
                slideView(sugar_level_layout)
            }
        }
        editTextSeekBarSetup(1, 20, edit_text_sugar_level, seek_bar_sugar_level)
        edit_text_sugar_level.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if ((s.isNullOrEmpty()) and (vf_recommendation.height != 0)) slideView(
                    vf_recommendation
                )
                if (!foodList.isNullOrEmpty() and checkbox_sugar_level.isChecked and !s.isNullOrEmpty()) {
                    updateRecommendation()
                }
            }
        })

        if (updateBool and !updateFinished) {
            appDatabaseViewModel.getMealWithFoods(args.selectedRecord?.id)
                .observe(viewLifecycleOwner, Observer { record ->

                    if (!record.isNullOrEmpty()) {
                        view.spinner_meal.setSelection(
                            resources.getStringArray(R.array.MealSpinner)
                                .indexOf(record[0].meal.type)
                        )
                        if (foodList.isNullOrEmpty()) {
                            for (food in record) {
                                foodList.add(FoodInMealItem(food.food, food.weight!!))
                            }
                            adapter.updateItems(foodList)
                            iterablePredictors = getGLCarbsKr(foodList)
                        }
                        if (record[0].meal.sugarLevel != null) {
                            view.checkbox_sugar_level.isChecked = true
                            edit_text_sugar_level.setText(record[0].meal.sugarLevel.toString())
                        }
                    }

                })

            val currentTime = getSelectedTimeInMilli()
            getTimeIntervalPredictors(currentTime)

            tv_title.text = this.resources.getString(R.string.UpdateRecord)
            tv_Time.text = args.selectedRecord?.time
            tv_Date.text = args.selectedRecord?.date
            btn_delete.visibility = View.VISIBLE
            btn_delete.setOnClickListener {
                deleteRecord()
            }
            updateFinished = true
        }
    }

    private fun getTimeIntervalPredictors(time: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            val isSixHoursLoaded = CoroutineScope(Dispatchers.IO).async {
                getSixHourPredictors(time)
            }
            val isTwelveHoursLoaded = CoroutineScope(Dispatchers.IO).async {
                getTwelveHourPredictors(time)
            }
            if (isSixHoursLoaded.await() && isTwelveHoursLoaded.await()) {
                if (checkbox_sugar_level.isChecked
                    and !edit_text_sugar_level.text.isNullOrEmpty()
                    and !foodList.isNullOrEmpty()
                ) updateRecommendation()
            }
        }
    }

    private suspend fun getTwelveHourPredictors(time: Long): Boolean {
        val records = appDatabaseViewModel.getMealWithFoods12HoursAgo(time)
        pv12 = getPv(records)
        return true
    }

    private suspend fun getSixHourPredictors(time: Long): Boolean {
        val records = appDatabaseViewModel.getMealWithFoods6HoursAgo(time)
        sixHoursPredictors = getSixHoursPredictors(records)
        return true
    }

    private fun deleteRecord() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(this.resources.getString(R.string.Yes)) { _, _ ->
            appDatabaseViewModel.deleteMealRecord(args.selectedRecord?.id)
            args.selectedRecord?.let { appDatabaseViewModel.deleteRecord(it) }
            findNavController().navigate(R.id.action_mealAddRecord_to_homePage)
        }
        builder.setNegativeButton(this.resources.getString(R.string.No)) { _, _ ->
        }
        builder.setTitle(this.resources.getString(R.string.DeleteRecord))
        builder.setMessage(this.resources.getString(R.string.AreUSureDeleteRecord))
        builder.create().show()
    }

    private fun addRecord(sugarLevelSubmit: Boolean) {

        GlobalScope.launch(Dispatchers.Main) {

            val category = "meal_table"

            val type = spinner_meal.selectedItem.toString()
            val mainInfo = type

            val sugarLevel = if (sugarLevelSubmit) {
                getRecommendation()
                edit_text_sugar_level.text.toString().toDouble()
            } else {
                null
            }

            val time = tv_Time.text.toString()
            val date = tv_Date.text.toString()
            val dateInMilli = convertDateToMils("$time $date")

            val recordEntity = RecordEntity(
                null, category, mainInfo, dateInMilli, time, date,
                dateSubmit, false
            )
            val mealEntity = MealEntity(null, type, sugarLevel, hyperglycemiaChance)

            appDatabaseViewModel.addRecord(recordEntity, mealEntity, foodList)
            findNavController().navigate(R.id.action_mealAddRecord_to_homePage)
        }
    }

    private fun updateRecord(sugarLevelSubmit: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            val type = spinner_meal.selectedItem.toString()

            val sugarLevel = if (sugarLevelSubmit) {
                getRecommendation()
                edit_text_sugar_level.text.toString().toDouble()
            } else {
                null
            }

            val time = tv_Time.text.toString()
            val date = tv_Date.text.toString()
            val dateInMilli = convertDateToMils("$time $date")

            val recordEntity = RecordEntity(
                args.selectedRecord?.id,
                args.selectedRecord?.category,
                type,
                dateInMilli,
                time,
                date,
                args.selectedRecord?.dateSubmit,
                args.selectedRecord?.fullDay
            )
            val mealEntity =
                MealEntity(args.selectedRecord?.id, type, sugarLevel, hyperglycemiaChance)

            appDatabaseViewModel.updateRecord(recordEntity, mealEntity, foodList)
            findNavController().previousBackStackEntry?.savedStateHandle?.set("mealTypeKey", type)
            findNavController().popBackStack()
        }
    }

    private fun updateRecommendation() {
        if (appType == "GDMRCT") {
            vf_recommendation.displayedChild = 0
            if (vf_recommendation.height == 0) slideView(vf_recommendation)
            CoroutineScope(Dispatchers.Main).launch {
                val result = CoroutineScope(Dispatchers.IO).async {
                    getRecommendation()
                }
                try {
                    vf_recommendation.displayedChild = result.await()
                    recommendationsAdapter.updateItems(recommendations)
                    pager_recommendations_indicator.setViewPager(pager_recommendations)
                } catch (e: Exception) {
                }
            }
        }
    }

    private suspend fun getRecommendation(): Int {
        return if ((edit_text_sugar_level != null) and (spinner_meal != null)) {

            val slBefore = edit_text_sugar_level.text.toString().toDouble()
            val mealType = spinner_meal.selectedItem.toString()

            hyperglycemiaChance = setTwoDigits(
                predictSL(
                    context = requireContext(),
                    bg0 = slBefore.toFloat(),
                    iterablePredictors = iterablePredictors.first,
                    sixHoursPredictors = sixHoursPredictors,
                    pvTwelveHours = pv12,
                    mealType = mealType,
                    bmi = bmi,
                    hbA1C = hbA1C,
                    tg = tg,
                    hol = hol,
                    weight = weightBeforePregnancy,
                    age = age,
                    glucoseNt = glucoseNt,
                    analysisTime = analysisTime
                )
            )

            val highGI = checkGI(foodList)
            val manyCarbs = checkCarbs(mealType, foodList)
            val highBGBefore = checkSLBefore(slBefore)
            val highBGPredict = checkHyperglycemia(hyperglycemiaChance!!)
            try {
                recommendations = getMessage(
                    highGI, manyCarbs, highBGBefore,
                    iterablePredictors.second, highBGPredict, resources
                )
            } catch (e: java.lang.IllegalStateException) {
            }

            val result = if (highBGPredict) {
                2
            } else
                1
            result
        } else
            0
    }

    private fun getSelectedTimeInMilli(): Long {
        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()
        return convertDateToMils("$time $date")
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context =
            ContextThemeWrapper(requireContext(), R.style.InsulinTheme)
        return inflater.cloneInContext(contextThemeWrapper)
    }

    override fun updateRecommendationWeight(position: Int) {

        val selectWeightDialog = SelectWeightDialog(requireContext(), foodList[position].weight)
        selectWeightDialog.show(requireFragmentManager(), "weight select dialog")

        setFragmentResultListener("requestKey") { _, bundle ->
            val result = bundle.getString("resultKey")
            foodList[position].weight = result!!.toDouble()
            adapter.updateItems(foodList)
            iterablePredictors = getGLCarbsKr(foodList)
            if (checkbox_sugar_level.isChecked and
                !edit_text_sugar_level.text.isNullOrEmpty()
            ) {
                updateRecommendation()
            }
        }
    }

    private fun setupSpinnerPreferences(date: String) {
        val id = if (updateBool) args.selectedRecord?.id else 0
        appDatabaseViewModel.checkMealType(date, id).observe(viewLifecycleOwner) { types ->
            val items = mutableListOf<Int>()
            for (pref in types) {
                if ((pref != spinnerStringArray[3]))
                    items.add(spinnerStringArray.indexOf(pref))
            }
            spinnerAdapter.setItemsToHide(items)
            if (spinner_meal.selectedItem.toString() in types) spinner_meal.setSelection(3)
        }

    }

}