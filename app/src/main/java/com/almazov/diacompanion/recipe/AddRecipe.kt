package com.almazov.diacompanion.recipe

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.FoodEntity
import com.almazov.diacompanion.meal.FoodInMealItem
import com.almazov.diacompanion.meal.FoodInMealListAdapter
import com.almazov.diacompanion.meal.SelectWeightDialog
import com.almazov.diacompanion.meal.SwipeDeleteFood
import kotlinx.android.synthetic.main.fragment_add_recipe.*
import kotlinx.android.synthetic.main.fragment_add_recipe.view.*
import kotlinx.android.synthetic.main.fragment_meal_add_record.*
import kotlinx.android.synthetic.main.fragment_meal_add_record.btn_add_food
import kotlinx.android.synthetic.main.fragment_meal_add_record.btn_save

class AddRecipe : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel

    var foodList = mutableListOf<FoodInMealItem>()
    var lastFood: String = ""
    lateinit var adapter: FoodInMealListAdapter

    var updateBool: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        view.edit_text_recipe_name.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        view.spinner_recipe.adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.MealSpinner,
            R.layout.spinner_item
        )

        val recyclerView = view.recycler_view_food_in_recipe
        adapter = FoodInMealListAdapter(foodList)

        val swipeDeleteFood = object : SwipeDeleteFood(requireContext()) {
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

        view.btn_add_food.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_addRecipe_to_foodList)
        }

        view.btn_save.setOnClickListener {

            if (edit_text_recipe_name.text.toString().isNotBlank() and foodList.isNullOrEmpty()) {
                if (updateBool) {
                } else {
                }
                findNavController().popBackStack()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        super.onViewCreated(view, savedInstanceState)
    }

}