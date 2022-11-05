package com.almazov.diacompanion.meal

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_food_list.view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FoodlList : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    val adapter = FoodlListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_food_list, container, false)

        val recyclerView = view.recycler_view_meal
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        appDatabaseViewModel = ViewModelProvider(this).get(AppDatabaseViewModel::class.java)

        lifecycleScope.launch{
            appDatabaseViewModel.readFoodPaged.collectLatest {
                adapter.submitData(it)
            }
        }

        view.edit_text_search_food.setOnClickListener{
            /*Navigation.findNavController(view).popBackStack(R.id.mealAddRecord,false,true);*/
        }

        return view
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.MealTheme)
        return inflater.cloneInContext(contextThemeWrapper)
    }

}