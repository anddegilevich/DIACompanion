package com.almazov.diacompanion.meal

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_food_list.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FoodList : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private lateinit var adapter: FoodListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_food_list, container, false)
        val recyclerView = view.recycler_view_food

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        adapter = FoodListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        readFood()

        view.edit_text_search_food.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(string: Editable) {
                if (string.isNullOrBlank()) readFood()
                else {
                    filterFood(string.toString().trim())
                }
            }

            override fun beforeTextChanged(string: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(string: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })

        return view
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.MealTheme)
        return inflater.cloneInContext(contextThemeWrapper)
    }

    private fun filterFood(filter: String) {
        lifecycleScope.launch{
            appDatabaseViewModel.readFoodPagedFilter(filter).collectLatest {
                view?.recycler_view_food?.smoothScrollToPosition(0)
                adapter.submitData(it)
            }
        }
    }

    private fun readFood() {
        lifecycleScope.launch{
            appDatabaseViewModel.readFoodPaged().collectLatest {
                view?.recycler_view_food?.smoothScrollToPosition(0)
                adapter.submitData(it)
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

        super.onDestroy()
    }

}