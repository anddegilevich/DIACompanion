package com.almazov.diacompanion.add_record

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.convertDateToMils
import com.almazov.diacompanion.base.editTextSeekBarSetup
import com.almazov.diacompanion.base.timeDateSelectSetup
import com.almazov.diacompanion.data.*
import com.almazov.diacompanion.base.CustomStringAdapter
import kotlinx.android.synthetic.main.fragment_meal_add_record.*
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.*
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.btn_delete
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.btn_save
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.edit_text_sugar_level
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.seek_bar_sugar_level
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.tv_Date
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.tv_Time
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.tv_title


class SugarLevelAddRecord : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var dateSubmit: Long? = null
    private var updateBool: Boolean = false
    private val args by navArgs<SugarLevelAddRecordArgs>()
    private lateinit var spinnerAdapter: CustomStringAdapter
    private lateinit var spinnerStringArray: Array<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        updateBool = args.selectedRecord != null
        return inflater.inflate(R.layout.fragment_sugar_level_add_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        val sugarLevelMin = 1
        val sugarLevelMax = 20

        editTextSeekBarSetup(sugarLevelMin, sugarLevelMax, edit_text_sugar_level, seek_bar_sugar_level)


        spinnerStringArray = resources.getStringArray(R.array.SugarLevelSpinner)
        spinnerAdapter = CustomStringAdapter(requireContext(),
            R.layout.spinner_item, spinnerStringArray
        )
        spinner_sugar_level.adapter = spinnerAdapter

        tv_Date.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                setupSpinnerPreferences(s.toString())
            }
        })
        dateSubmit = timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        if (updateBool)
        {
            appDatabaseViewModel.readSugarLevelRecord(args.selectedRecord?.id).observe(viewLifecycleOwner, Observer{record ->
                edit_text_sugar_level.setText(record.sugarLevel.toString())
                spinner_sugar_level.setSelection(resources.getStringArray(R.array.SugarLevelSpinner).indexOf(record.preferences))
                checkbox_physical_act.isChecked = record.wasPhysicalAct == true
            })
            tv_title.text = this.resources.getString(R.string.UpdateRecord)
            tv_Time.text = args.selectedRecord?.time
            tv_Date.text = args.selectedRecord?.date
            btn_delete.visibility = View.VISIBLE
            btn_delete.setOnClickListener {
                deleteRecord()
            }
        }

        btn_save.setOnClickListener {
            val sugarLevelSubmitted = !edit_text_sugar_level.text.toString().isNullOrBlank()
            if (sugarLevelSubmitted) {
                if (updateBool) {
                    updateRecord()
                    findNavController().popBackStack()
                } else {
                    addRecord()
                    Navigation.findNavController(view).navigate(R.id.action_sugarLevelAddRecord_to_homePage)
                }
            }
        }
    }

    private fun addRecord(){
        val category = "sugar_level_table"

        val sugarLevel = edit_text_sugar_level.text.toString()
        val preferences = spinner_sugar_level.selectedItem.toString()
        val wasPhysicalAct = checkbox_physical_act.isChecked
        val unit = this.resources.getString(R.string.mmoll)
        val mainInfo = "$sugarLevel $unit"

        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()
        val dateInMilli = convertDateToMils("$time $date")

        val recordEntity = RecordEntity(null, category, mainInfo,dateInMilli, time, date,
            dateSubmit,false)
        val sugarLevelEntity = SugarLevelEntity(null,sugarLevel.toDouble(),preferences,wasPhysicalAct)

        appDatabaseViewModel.addRecord(recordEntity,sugarLevelEntity)
    }

    private fun updateRecord(){
        val sugarLevel = edit_text_sugar_level.text.toString()
        val preferences = spinner_sugar_level.selectedItem.toString()
        val wasPhysicalAct = checkbox_physical_act.isChecked
        val unit = this.resources.getString(R.string.mmoll)
        val mainInfo = "$sugarLevel $unit"

        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()
        val dateInMilli = convertDateToMils("$time $date")

        val recordEntity = RecordEntity(args.selectedRecord?.id, args.selectedRecord?.category, mainInfo,dateInMilli, time, date,
            args.selectedRecord?.dateSubmit,args.selectedRecord?.fullDay)
        val sugarLevelEntity = SugarLevelEntity(args.selectedRecord?.id,sugarLevel.toDouble(),preferences,wasPhysicalAct)
        appDatabaseViewModel.updateRecord(recordEntity,sugarLevelEntity)
    }

    private fun deleteRecord() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(this.resources.getString(R.string.Yes)) {_, _ ->
            appDatabaseViewModel.deleteSugarLevelRecord(args.selectedRecord?.id)
            args.selectedRecord?.let { appDatabaseViewModel.deleteRecord(it) }
            findNavController().popBackStack()
        }
        builder.setNegativeButton(this.resources.getString(R.string.No)) {_, _ ->
        }
        builder.setTitle(this.resources.getString(R.string.DeleteRecord))
        builder.setMessage(this.resources.getString(R.string.AreUSureDeleteRecord))
        builder.create().show()
    }

    private fun setupSpinnerPreferences(date: String) {
        val id = if (updateBool) args.selectedRecord?.id else 0
        appDatabaseViewModel.checkSugarLevelPrefs(date,id).
        observe(viewLifecycleOwner, Observer { prefs ->
            val items = mutableListOf<Int>()
            for (pref in prefs) {
                if ((pref != spinnerStringArray[0]) and (pref != spinnerStringArray[5]))
                items.add(spinnerStringArray.indexOf(pref))
            }
            spinnerAdapter.setItemsToHide(items)
            if (spinner_sugar_level.selectedItem.toString() in prefs) spinner_sugar_level.setSelection(0)
        })

    }

}