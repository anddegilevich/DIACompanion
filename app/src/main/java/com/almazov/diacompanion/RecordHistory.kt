package com.almazov.diacompanion

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_record_history.view.*

class RecordHistory : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_record_history, container, false)

        val adapter = RecordListAdapter()
        val recyclerView = view.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        appDatabaseViewModel = ViewModelProvider(this).get(AppDatabaseViewModel::class.java)
        appDatabaseViewModel.readALlData.observe(viewLifecycleOwner, Observer{record ->
            adapter.setData(record)
        })

        return view
    }

}