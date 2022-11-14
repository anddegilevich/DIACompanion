package com.almazov.diacompanion.record_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.DateClass
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_record_history.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecordHistory : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private lateinit var adapter: DateListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_record_history, container, false)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        adapter = DateListAdapter(appDatabaseViewModel, viewLifecycleOwner)

        val recyclerView = view.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        readAllRecords()

        view.btn_options.setOnClickListener{
            showPopup(view.btn_options)
        }

        return view
    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.record_history_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.all -> {
                    readAllRecords()
                }
                R.id.sugar_level -> {
                    filterRecords("sugar_level_table")
                }
                R.id.insulin -> {
                    filterRecords("insulin_table")
                }
                R.id.meal -> {
                    filterRecords("meal_table")
                }
                R.id.workout -> {
                    filterRecords("workout_table")
                }
                R.id.sleep -> {
                    filterRecords("sleep_table")
                }
                R.id.weight -> {
                    filterRecords("weight_table")
                }
                R.id.ketone -> {
                    filterRecords("ketone_table")
                }

            }
            false
        }

        popup.show()
    }

    private fun filterRecords(filter: String) {

        adapter.filter = filter
        lifecycleScope.launch{
            appDatabaseViewModel.readDatesPaged().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun readAllRecords() {

        adapter.filter = ""
        lifecycleScope.launch{
            appDatabaseViewModel.readDatesPaged().collectLatest {
                adapter.submitData(it)
            }
        }
    }


    private fun applyChanges(){
        val changes = adapter.fullDaysChanges
        GlobalScope.launch {
            for (change in changes) {
                appDatabaseViewModel.updateFullDays(change.first, change.second)
            }
        }
    }

    override fun onDestroy() {
        applyChanges()
        super.onDestroy()
    }

    override fun onPause() {
        applyChanges()
        super.onPause()
    }

}