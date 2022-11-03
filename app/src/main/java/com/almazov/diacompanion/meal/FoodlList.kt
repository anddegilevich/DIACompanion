package com.almazov.diacompanion.meal

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_food_list.view.*
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
            /*appDatabaseViewModel.readAllPaged.collectLatest {
                adapter.submitData(it)
            }*/
        }

        return view
    }

    /*fun prePop() {
        lifecycleScope.launch{
            for (i in 0..200000) {
                appDatabaseViewModel.addRecord(RecordEntity(null,"sleep_table","sleep$i",1667377680000+i.toLong(),"10:00","20.01.2000",1667377680000,false), SleepEntity(null,0.toDouble()))
            }
        }
    }*/

}