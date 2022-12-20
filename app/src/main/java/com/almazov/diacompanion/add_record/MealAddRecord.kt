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
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.*
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.FoodEntity
import com.almazov.diacompanion.data.MealEntity
import com.almazov.diacompanion.data.RecordEntity
import com.almazov.diacompanion.meal.*
import kotlinx.android.synthetic.main.fragment_insulin_add_record.*
import kotlinx.android.synthetic.main.fragment_meal_add_record.*
import kotlinx.android.synthetic.main.fragment_meal_add_record.btn_delete
import kotlinx.android.synthetic.main.fragment_meal_add_record.btn_save
import kotlinx.android.synthetic.main.fragment_meal_add_record.tv_Date
import kotlinx.android.synthetic.main.fragment_meal_add_record.tv_Time
import kotlinx.android.synthetic.main.fragment_meal_add_record.tv_title
import kotlinx.android.synthetic.main.fragment_meal_add_record.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*


class MealAddRecord : Fragment(), FoodInMealListAdapter.InterfaceFoodInMeal {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var dateSubmit: Long? = null
    private val args by navArgs<MealAddRecordArgs>()

    var foodList = mutableListOf<FoodInMealItem>()
    var lastFood: Int = 0
    lateinit var adapter: FoodInMealListAdapter
    var updateBool: Boolean = false
    var updateFinished: Boolean = false

    private var bmi: Double? = null
    private var protein: Double? = null
    private var glCarbsKr: List<Double?> = emptyList()
    private var sugarLevelPredicted: Double? = null

    private lateinit var spinnerAdapter: CustomStringAdapter
    private lateinit var spinnerStringArray: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        bmi = sharedPreferences!!.getFloat("BMI", 20f).toDouble()

        updateBool = args.selectedRecord != null
        return inflater.inflate(R.layout.fragment_meal_add_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        spinnerStringArray = resources.getStringArray(R.array.MealSpinner)
        spinnerAdapter = CustomStringAdapter(requireContext(),
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
                    and !edit_text_sugar_level.text.isNullOrEmpty()) updateRecommendation()
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
                getProteinDate(dateInMilli)
            }
        }
        dateSubmit = timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        tv_Time.addTextChangedListener(textWatcher)
        tv_Date.addTextChangedListener(textWatcher)
        val recyclerView = view.recycler_view_food_in_meal
        adapter = FoodInMealListAdapter(foodList, this)

        val swipeDeleteFood = object : SwipeDeleteFood(requireContext(), R.color.blue_dark) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        foodList.removeAt(viewHolder.adapterPosition)
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                        if (foodList.isNullOrEmpty()) {
                            slideView(vf_recommendation)} else
                        if (checkbox_sugar_level.isChecked
                            and !edit_text_sugar_level.text.isNullOrEmpty()) {
                            glCarbsKr = getGLCarbsKr(foodList)
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

        Navigation.findNavController(view).currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FoodEntity>("foodKey")?.observe(viewLifecycleOwner) {

                if (it != null) {

                    var foodAlreadyInList = false
                    for (food in foodList) {
                        if (it.name == food.foodEntity.name) foodAlreadyInList = true
                    }

                    if (!foodAlreadyInList) {
                        lastFood = it.idFood!!
                        val selectWeightDialog = SelectWeightDialog(requireContext())
                        selectWeightDialog.isCancelable = false
                        selectWeightDialog.show(requireFragmentManager(), "weight select dialog")

                        setFragmentResultListener("requestKey") { _, bundle ->
                            val result = bundle.getString("resultKey")
                            foodList.add(FoodInMealItem(it, result!!.toDouble()))
                            adapter.notifyItemInserted(foodList.size)
                            glCarbsKr = getGLCarbsKr(foodList)
                            if (checkbox_sugar_level.isChecked and
                                !edit_text_sugar_level.text.isNullOrEmpty()
                            ) {
                                updateRecommendation()
                            }
                        }
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
                if (!foodList.isNullOrEmpty() and !edit_text_sugar_level.text.isNullOrEmpty()) slideView(vf_recommendation)
                slideView(sugar_level_layout)
            }
        }
        editTextSeekBarSetup(1, 20, edit_text_sugar_level, seek_bar_sugar_level )
        edit_text_sugar_level.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if ((s.isNullOrEmpty()) and (vf_recommendation.height != 0)) slideView(vf_recommendation)
                if (!foodList.isNullOrEmpty() and !s.isNullOrEmpty()) {
                    updateRecommendation()
                }
            }
        })

        if (updateBool and !updateFinished)
        {
            appDatabaseViewModel.getMealWithFoods(args.selectedRecord?.id).observe(viewLifecycleOwner, Observer{record ->

                if (!record.isNullOrEmpty()) {
                    view.spinner_meal.setSelection(
                        resources.getStringArray(R.array.MealSpinner).indexOf(record[0].meal.type)
                    )
                    if (foodList.isNullOrEmpty()) {
                        for (food in record) {
                            foodList.add(FoodInMealItem(food.food, food.weight!!))
                            adapter.notifyItemInserted(foodList.size)
                        }
                        glCarbsKr = getGLCarbsKr(foodList)
                    }
                    if (record[0].meal.sugarLevel != null) {
                        view.checkbox_sugar_level.isChecked = true
                        edit_text_sugar_level.setText(record[0].meal.sugarLevel.toString())
                    }
                }

            })

            val currentTime = getSelectedTimeInMilli()
            getProteinDate(currentTime)

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

    private fun getProteinDate(time: Long) {
        appDatabaseViewModel.getMealWithFoods6HoursAgo(time).observe(viewLifecycleOwner, Observer{record ->

                protein = getProtein(record)
                if (checkbox_sugar_level.isChecked
                    and !edit_text_sugar_level.text.isNullOrEmpty()
                    and !foodList.isNullOrEmpty()) updateRecommendation()
        })
    }

    private fun deleteRecord() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(this.resources.getString(R.string.Yes)) {_, _ ->
            appDatabaseViewModel.deleteMealRecord(args.selectedRecord?.id)
            args.selectedRecord?.let { appDatabaseViewModel.deleteRecord(it) }
            findNavController().navigate(R.id.action_mealAddRecord_to_homePage)
        }
        builder.setNegativeButton(this.resources.getString(R.string.No)) {_, _ ->
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
            val mealEntity = MealEntity(null, type, sugarLevel, sugarLevelPredicted)

            appDatabaseViewModel.addRecord(recordEntity, mealEntity, foodList)
            findNavController().navigate(R.id.action_mealAddRecord_to_homePage)
        }
    }

    private fun updateRecord(sugarLevelSubmit: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
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

        val recordEntity = RecordEntity(args.selectedRecord?.id, args.selectedRecord?.category, mainInfo,dateInMilli, time, date,
            args.selectedRecord?.dateSubmit,args.selectedRecord?.fullDay)
        val mealEntity = MealEntity(args.selectedRecord?.id,type, sugarLevel, sugarLevelPredicted)

        appDatabaseViewModel.updateRecord(recordEntity,mealEntity,foodList)
        findNavController().previousBackStackEntry?.savedStateHandle?.set("mealTypeKey", type)
        findNavController().popBackStack()
        }
    }

    private fun updateRecommendation() {
        vf_recommendation.displayedChild = 0
        if (vf_recommendation.height == 0) slideView(vf_recommendation)
        GlobalScope.launch(Dispatchers.Main) {
            val result = GlobalScope.async(Dispatchers.Default) {
            getRecommendation()
        }
        if (vf_recommendation != null) vf_recommendation.displayedChild = result.await()
        }
    }

    private suspend fun getRecommendation(): Int {
        return if ((edit_text_sugar_level != null) and (spinner_meal != null)) {
            sugarLevelPredicted = setTwoDigits(predictSL(
                requireContext(), edit_text_sugar_level.text.toString().toDouble(),
                glCarbsKr, protein, spinner_meal.selectedItem.toString(), bmi
            ))

            val result = if (sugarLevelPredicted!! > 6.8) {
                2
            } else
                1
            result
        } else
            0
    }

    private fun  getSelectedTimeInMilli(): Long {
        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()
        return convertDateToMils("$time $date")
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.InsulinTheme)
        return inflater.cloneInContext(contextThemeWrapper)
    }

    override fun updateRecommendationWeight(position: Int, weight: Double) {
        foodList[position].weight = weight
        glCarbsKr = getGLCarbsKr(foodList)
        if (checkbox_sugar_level.isChecked and
            !edit_text_sugar_level.text.isNullOrEmpty()
        ) {
            updateRecommendation()
        }
    }

    private fun setupSpinnerPreferences(date: String) {
        val id = if (updateBool) args.selectedRecord?.id else 0
        appDatabaseViewModel.checkMealType(date,id).
        observe(viewLifecycleOwner, Observer { types ->
            val items = mutableListOf<Int>()
            for (pref in types) {
                if ((pref != spinnerStringArray[3]))
                    items.add(spinnerStringArray.indexOf(pref))
            }
            spinnerAdapter.setItemsToHide(items)
            if (spinner_meal.selectedItem.toString() in types) spinner_meal.setSelection(3)
        })

    }
}