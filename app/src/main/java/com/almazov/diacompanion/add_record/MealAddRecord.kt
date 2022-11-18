package com.almazov.diacompanion.add_record

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.convertDateToMils
import com.almazov.diacompanion.base.editTextSeekBarSetup
import com.almazov.diacompanion.base.slideView
import com.almazov.diacompanion.base.timeDateSelectSetup
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.FoodEntity
import com.almazov.diacompanion.data.MealEntity
import com.almazov.diacompanion.data.RecordEntity
import com.almazov.diacompanion.meal.FoodInMealItem
import com.almazov.diacompanion.meal.FoodInMealListAdapter
import com.almazov.diacompanion.meal.SelectWeightDialog
import com.almazov.diacompanion.meal.SwipeDeleteFood
import kotlinx.android.synthetic.main.fragment_meal_add_record.*
import kotlinx.android.synthetic.main.fragment_meal_add_record.btn_delete
import kotlinx.android.synthetic.main.fragment_meal_add_record.btn_save
import kotlinx.android.synthetic.main.fragment_meal_add_record.edit_text_sugar_level
import kotlinx.android.synthetic.main.fragment_meal_add_record.seek_bar_sugar_level
import kotlinx.android.synthetic.main.fragment_meal_add_record.tv_Date
import kotlinx.android.synthetic.main.fragment_meal_add_record.tv_Time
import kotlinx.android.synthetic.main.fragment_meal_add_record.tv_title
import kotlinx.android.synthetic.main.fragment_meal_add_record.view.*


class MealAddRecord : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var dateSubmit: Long? = null
    private var bmi: Double? = 0.0
    private val args by navArgs<MealAddRecordArgs>()

    var foodList = mutableListOf<FoodInMealItem>()
    var lastFood: String = ""
    lateinit var adapter: FoodInMealListAdapter
    var updateBool: Boolean = false
    var updateFinished: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences = context?.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        bmi = sharedPreferences!!.getFloat("BMI", 1f).toDouble()

        updateBool = args.selectedRecord != null
        return inflater.inflate(R.layout.fragment_meal_add_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        spinner_meal.adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.MealSpinner,
            R.layout.spinner_item
        )

        dateSubmit = timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        val recyclerView = view.recycler_view_food_in_meal
        adapter = FoodInMealListAdapter(foodList)

        val swipeDeleteFood = object : SwipeDeleteFood(requireContext(), R.color.purple_dark) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        foodList.removeAt(viewHolder.adapterPosition)
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeDeleteFood)
        touchHelper.attachToRecyclerView(recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

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
                    }
                    if (record[0].meal.sugarLevel != null) {
                        view.checkbox_sugar_level.isChecked = true
                        edit_text_sugar_level.setText(record[0].meal.sugarLevel.toString())
                    }
                }

            })

            tv_title.text = this.resources.getString(R.string.UpdateRecord)
            tv_Time.text = args.selectedRecord?.time
            tv_Date.text = args.selectedRecord?.date
            btn_delete.visibility = View.VISIBLE
            btn_delete.setOnClickListener {
                deleteRecord()
            }
            updateFinished = true
        }

        Navigation.findNavController(view).currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FoodEntity>("foodKey")?.observe(viewLifecycleOwner) {

                var foodAlreadyInList = false
                for (food in foodList) {
                    if (it.name == food.foodEntity.name) foodAlreadyInList = true
                }
                val lastFoodBool = lastFood == it.name

                if (!foodAlreadyInList and !lastFoodBool) {
                    lastFood = it.name!!
                    val selectWeightDialog = SelectWeightDialog(requireContext())
                    selectWeightDialog.isCancelable = false
                    selectWeightDialog.show(requireFragmentManager(), "weight select dialog")

                    setFragmentResultListener("requestKey") { key, bundle ->
                        val result = bundle.getString("resultKey")
                        foodList.add(FoodInMealItem(it, result!!.toDouble()))
                        adapter.notifyItemInserted(foodList.size)
                    }
                }
            }

        btn_add_food.setOnClickListener {
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
                    findNavController().popBackStack()
                } else {
                    addRecord(sugarLevelSubmit)
                    Navigation.findNavController(view).navigate(R.id.action_mealAddRecord_to_homePage)
                }
            }
        }

        checkbox_sugar_level.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                slideView(sugar_level_layout,0,200)
            } else {
                slideView(sugar_level_layout,200,0)
            }
        }
        editTextSeekBarSetup(1, 20, edit_text_sugar_level, seek_bar_sugar_level )
    }

    private fun deleteRecord() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(this.resources.getString(R.string.Yes)) {_, _ ->
            appDatabaseViewModel.deleteMealRecord(args.selectedRecord?.id)
            args.selectedRecord?.let { appDatabaseViewModel.deleteRecord(it) }
            findNavController().popBackStack()
        }
        builder.setNegativeButton(this.resources.getString(R.string.No)) {_, _ ->
        }
        builder.setTitle(this.resources.getString(R.string.DeleteRecord))
        builder.setMessage(this.resources.getString(R.string.AreUSureDeleteRecord))
        builder.create().show()
    }

    private fun addRecord(sugarLevelSubmit: Boolean) {
        val category = "meal_table"

        val type = spinner_meal.selectedItem.toString()
        val mainInfo = type

        val sugarLevel = if (sugarLevelSubmit) {
            edit_text_sugar_level.text.toString().toDouble()
        } else {
            null
        }

        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()
        val dateInMilli = convertDateToMils("$time $date")

        val recordEntity = RecordEntity(null, category, mainInfo,dateInMilli, time, date,
            dateSubmit,false)
        val mealEntity = MealEntity(null,type, sugarLevel)

        appDatabaseViewModel.addRecord(recordEntity,mealEntity,foodList)
    }

    private fun updateRecord(sugarLevelSubmit: Boolean) {
        val type = spinner_meal.selectedItem.toString()
        val mainInfo = type

        val sugarLevel = if (sugarLevelSubmit) {
            edit_text_sugar_level.text.toString().toDouble()
        } else {
            null
        }

        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()
        val dateInMilli = convertDateToMils("$time $date")

        val recordEntity = RecordEntity(args.selectedRecord?.id, args.selectedRecord?.category, mainInfo,dateInMilli, time, date,
            args.selectedRecord?.dateSubmit,args.selectedRecord?.fullDay)
        val mealEntity = MealEntity(args.selectedRecord?.id,type, sugarLevel)

        appDatabaseViewModel.updateRecord(recordEntity,mealEntity,foodList)
    }




    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.MealTheme)
        return inflater.cloneInContext(contextThemeWrapper)
    }
}