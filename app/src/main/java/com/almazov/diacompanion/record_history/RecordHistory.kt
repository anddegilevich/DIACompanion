package com.almazov.diacompanion.record_history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_record_history.*
import kotlinx.android.synthetic.main.fragment_record_history.view.*
import kotlinx.coroutines.flow.observeOn

class RecordHistory : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    val adapter = RecordListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_record_history, container, false)

        val recyclerView = view.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        appDatabaseViewModel = ViewModelProvider(this).get(AppDatabaseViewModel::class.java)
        appDatabaseViewModel.readALlData.observe(viewLifecycleOwner, Observer{record ->
            adapter.setData(record)
        })

        view.btn_options.setOnClickListener{
            filterRecords()
        }

        return view
    }

    private fun filterRecords() {
        appDatabaseViewModel.filterDatabase("insulin_table").observe(viewLifecycleOwner) {
                list -> list.let { adapter.setData(it) }
        }
    }


}