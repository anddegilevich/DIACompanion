package com.almazov.diacompanion

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.almazov.diacompanion.categories.CategoriesAdapter
import com.almazov.diacompanion.categories.Category
import com.almazov.diacompanion.categories.CategoryClickListener
import com.almazov.diacompanion.categories.categoryList
import com.almazov.diacompanion.databinding.FragmentRecordsCategoriesBinding


class RecordsCategories : Fragment(), CategoryClickListener {

    private lateinit var binding: FragmentRecordsCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordsCategoriesBinding.inflate(layoutInflater)
        val view = binding.root

        val main = this

        categoryList.clear()
        populateCategories()

        binding.RecyclerView.apply {
            layoutManager = GridLayoutManager(context,2)
            adapter = CategoriesAdapter(categoryList, main)
        }

        return view
    }

    override fun onClick(category: Category)
    {
        Navigation.findNavController(requireView()).navigate(category.action)
    }

    private fun populateCategories() {
        val sugarLevel = Category(
            R.drawable.sugar_level,
            R.string.SugarLevel,
            resources.getColor(R.color.red),
            resources.getColor(R.color.red_dark),
            R.id.action_recordsCategories_to_sugarLevelAddRecord
        )
        categoryList.add(sugarLevel)

        val insulin = Category(
            R.drawable.insulin,
            R.string.Insulin,
            resources.getColor(R.color.blue),
            resources.getColor(R.color.blue_dark),
            R.id.action_recordsCategories_to_insulinAddRecord
        )
        categoryList.add(insulin)

        val meal = Category(
            R.drawable.meal,
            R.string.Meal,
            resources.getColor(R.color.purple),
            resources.getColor(R.color.purple_dark),
            R.id.action_recordsCategories_to_mealAddRecord
        )
        categoryList.add(meal)

        val workout = Category(
            R.drawable.workout,
            R.string.Workout,
            resources.getColor(R.color.green),
            resources.getColor(R.color.green_dark),
            R.id.action_recordsCategories_to_workoutAddRecord
        )
        categoryList.add(workout)

        val sleep = Category(
            R.drawable.sleep,
            R.string.Sleep,
            resources.getColor(R.color.pink),
            resources.getColor(R.color.pink_dark),
            R.id.action_recordsCategories_to_sleepAddRecord
        )
        categoryList.add(sleep)

        val weight = Category(
            R.drawable.weight,
            R.string.Weight,
            resources.getColor(R.color.yellow),
            resources.getColor(R.color.yellow_dark),
            R.id.action_recordsCategories_to_weightAddRecord
        )
        categoryList.add(weight)

        val ketone = Category(
            R.drawable.ketone,
            R.string.Ketone,
            resources.getColor(R.color.orange),
            resources.getColor(R.color.orange_dark),
            R.id.action_recordsCategories_to_ketoneAddRecord
        )
        categoryList.add(ketone)
    }
}
