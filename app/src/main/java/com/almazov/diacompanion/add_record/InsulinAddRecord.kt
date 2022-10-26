package com.almazov.diacompanion.add_record

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.editTextSeekBarSetup
import com.almazov.diacompanion.base.timeDateSelectSetup
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.RecordEntity
import kotlinx.android.synthetic.main.fragment_insulin_add_record.*
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.btn_save
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.tv_Date
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.tv_Time

class InsulinAddRecord : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var dateSubmit: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_insulin_add_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        val insulinMin = 1
        val insulinMax = 40

        editTextSeekBarSetup(insulinMin, insulinMax, edit_text_insulin, seek_bar_insulin)

        spinner1_insulin.adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.InsulinSpinner1,
            R.layout.spinner_item
        )

        spinner2_insulin.adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.InsulinSpinner2,
            R.layout.spinner_item
        )

        timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        dateSubmit = tv_Date.text.toString()

        btn_save.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_insulinAddRecord_to_homePage)
            addRecord()
        }
    }

    private fun addRecord() {
        /*val category = "sugar_level_table"
        val idCategory = 1
        val mainInfo = edit_text_insulin.text.toString()
        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()

        val recordEntity = RecordEntity(0, category, null, mainInfo, time, date,
            dateSubmit,false)
        appDatabaseViewModel.addRecord(recordEntity)*/
    }


    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(),
            R.style.InsulinTheme
        )
        return inflater.cloneInContext(contextThemeWrapper)
    }



}