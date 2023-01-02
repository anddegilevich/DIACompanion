package com.almazov.diacompanion.meal

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.slideView
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_add_recipe.view.*
import kotlinx.android.synthetic.main.fragment_food_list.*
import kotlinx.android.synthetic.main.fragment_food_list.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FoodList : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private lateinit var adapter: FoodListAdapter

    private val args by navArgs<FoodListArgs>()
    private var filter: String = ""
    private var recipe: Boolean = false
    private var category: String = ""
    private var sortVar: String = "name"
    private var direction: String = "ASC"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        findNavController().previousBackStackEntry?.savedStateHandle?.set("foodKey", null)

        recipe = args.recipeBool

        if (!recipe) requireContext().setTheme(R.style.InsulinTheme)

        val view = inflater.inflate(R.layout.fragment_food_list, container, false)
        val recyclerView = view.recycler_view_food

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        adapter = FoodListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        filterFood()

        view.edit_text_search_food.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(string: Editable) {
                filter = string.toString()
                filterFood()
            }

            override fun beforeTextChanged(string: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(string: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })

        view.btn_options.setOnClickListener {
            slideView(sort_layout)
        }

        val categoryArray = resources.getStringArray(R.array.RecipeSpinner)
        val categoryList = categoryArray.toMutableList()
        categoryList.add(0, "Все категории")
        view.spinner_category.adapter = ArrayAdapter(requireContext(),R.layout.spinner_item,categoryList)

        view.spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                category = if (position == 0) "" else view.spinner_category.selectedItem.toString()
                filterFood()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        view.spinner_sort.adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.FoodSort,
            R.layout.spinner_item
        )
        view.spinner_sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                sortVar = if (position == 0) "name" else "gi"
                filterFood()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        view.btn_sort.setOnClickListener {

            direction = if (direction == "DESC") {
                view.btn_sort.rotation = -90F
                "ASC"
            } else {
                view.btn_sort.rotation = 90F
                "DESC"
            }
            filterFood()
        }

        return view
    }


    private fun filterFood() {
        lifecycleScope.launch{
            appDatabaseViewModel.readFoodPagedFilter(filter,recipe,category,sortVar,direction).collectLatest {
                view?.recycler_view_food?.smoothScrollToPosition(0)
                adapter.submitData(it)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun applyChanges(){
        val changes = adapter.favouriteChanges
        GlobalScope.launch {
            for (change in changes) {
                appDatabaseViewModel.updateFavourite(change.first, change.second)
            }
        }
    }

    override fun onDestroy() {
        applyChanges()
        requireContext().setTheme(R.style.Theme_DIACompanion)
        super.onDestroy()
    }

}