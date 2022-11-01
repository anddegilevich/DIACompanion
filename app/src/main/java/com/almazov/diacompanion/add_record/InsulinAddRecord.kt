package com.almazov.diacompanion.add_record

import android.app.AlertDialog
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.convertDateToMils
import com.almazov.diacompanion.base.editTextSeekBarSetup
import com.almazov.diacompanion.base.timeDateSelectSetup
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.InsulinEntity
import com.almazov.diacompanion.data.RecordEntity
import kotlinx.android.synthetic.main.fragment_insulin_add_record.*
import kotlinx.android.synthetic.main.fragment_insulin_add_record.tv_title

class InsulinAddRecord : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var dateSubmit: Long? = null
    var updateBool: Boolean = false
    private val args by navArgs<InsulinAddRecordArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        updateBool = args.selectedRecord != null
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

        dateSubmit = timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        if (updateBool)
        {
            appDatabaseViewModel.readInsulinRecord(args.selectedRecord?.id).observe(viewLifecycleOwner, Observer{record ->
                edit_text_insulin.setText(record.insulin.toString())
                spinner1_insulin.setSelection(resources.getStringArray(R.array.InsulinSpinner1).indexOf(record.type))
                spinner2_insulin.setSelection(resources.getStringArray(R.array.InsulinSpinner2).indexOf(record.preferences))
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
            val insulinSubmitted = !edit_text_insulin.text.toString().isNullOrBlank()
            if (insulinSubmitted) {
                if (updateBool) updateRecord() else addRecord()
                Navigation.findNavController(view).navigate(R.id.action_insulinAddRecord_to_homePage)
            }
        }
    }

    private fun addRecord(){
        val category = "insulin_table"

        val insulin = edit_text_insulin.text.toString()
        val type = spinner1_insulin.selectedItem.toString()
        val preferences = spinner2_insulin.selectedItem.toString()
        val unit = this.resources.getString(R.string.units)
        val mainInfo = "$insulin $unit"

        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()
        val dateInMilli = convertDateToMils("$time $date")

        val recordEntity = RecordEntity(null, category, mainInfo,dateInMilli, time, date,
            dateSubmit,false)
        val insulinEntity = InsulinEntity(null,insulin.toInt(),type,preferences)

        appDatabaseViewModel.addRecord(recordEntity,insulinEntity)
    }

    private fun updateRecord(){
        val insulin = edit_text_insulin.text.toString()
        val type = spinner1_insulin.selectedItem.toString()
        val preferences = spinner2_insulin.selectedItem.toString()
        val unit = this.resources.getString(R.string.units)
        val mainInfo = "$insulin $unit"

        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()
        val dateInMilli = convertDateToMils("$time $date")

        val recordEntity = RecordEntity(args.selectedRecord?.id, args.selectedRecord?.category, mainInfo,dateInMilli, time, date,
            args.selectedRecord?.dateSubmit,args.selectedRecord?.fullDay)
        val insulinEntity = InsulinEntity(args.selectedRecord?.id,insulin.toInt(),type,preferences)
        appDatabaseViewModel.updateRecord(recordEntity,insulinEntity)
    }

    private fun deleteRecord() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(this.resources.getString(R.string.Yes)) {_, _ ->
            appDatabaseViewModel.deleteInsulinRecord(args.selectedRecord?.id)
            args.selectedRecord?.let { appDatabaseViewModel.deleteRecord(it) }
            findNavController().navigate(R.id.action_insulinAddRecord_to_homePage)
        }
        builder.setNegativeButton(this.resources.getString(R.string.No)) {_, _ ->
        }
        builder.setTitle(this.resources.getString(R.string.DeleteRecord))
        builder.setMessage(this.resources.getString(R.string.AreUSureDeleteRecord))
        builder.create().show()
    }


    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(),
            R.style.InsulinTheme
        )
        return inflater.cloneInContext(contextThemeWrapper)
    }



}