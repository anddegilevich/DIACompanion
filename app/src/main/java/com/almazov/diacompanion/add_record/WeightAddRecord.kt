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
import com.almazov.diacompanion.data.RecordEntity
import com.almazov.diacompanion.data.WeightEntity
import kotlinx.android.synthetic.main.fragment_weight_add_record.*
import kotlinx.android.synthetic.main.fragment_weight_add_record.btn_delete
import kotlinx.android.synthetic.main.fragment_weight_add_record.tv_title


class WeightAddRecord : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var dateSubmit: Long? = null
    var updateBool: Boolean = false
    private val args by navArgs<WeightAddRecordArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        updateBool = args.selectedRecord != null
        return inflater.inflate(R.layout.fragment_weight_add_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        val weightMin = 1
        val weightMax = 150

        editTextSeekBarSetup(weightMin, weightMax, edit_text_weight, seek_bar_weight)

        dateSubmit = timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        if (updateBool)
        {
            appDatabaseViewModel.readWeightRecord(args.selectedRecord?.id).observe(viewLifecycleOwner, Observer{record ->
                edit_text_weight.setText(record.weight.toString())
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
            val weightSubmitted = !edit_text_weight.text.toString().isNullOrBlank()
            if (weightSubmitted) {
                if (updateBool) {
                    updateRecord()
                    findNavController().popBackStack()
                } else {
                    addRecord()
                    Navigation.findNavController(view).navigate(R.id.action_weightAddRecord_to_homePage)
                }
            }
        }
    }

    private fun addRecord(){
        val category = "weight_table"

        val weight = edit_text_weight.text.toString()
        val unit = this.resources.getString(R.string.kg)
        val mainInfo = "$weight $unit"

        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()
        val dateInMilli = convertDateToMils("$time $date")

        val recordEntity = RecordEntity(null, category, mainInfo,dateInMilli, time, date,
            dateSubmit,false)
        val weightEntity = WeightEntity(null,weight.toDouble())

        appDatabaseViewModel.addRecord(recordEntity,weightEntity)
    }

    private fun updateRecord(){
        val weight = edit_text_weight.text.toString()
        val unit = this.resources.getString(R.string.kg)
        val mainInfo = "$weight $unit"

        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()
        val dateInMilli = convertDateToMils("$time $date")

        val recordEntity = RecordEntity(args.selectedRecord?.id, args.selectedRecord?.category, mainInfo,dateInMilli, time, date,
            args.selectedRecord?.dateSubmit,args.selectedRecord?.fullDay)
        val weightEntity = WeightEntity(args.selectedRecord?.id,weight.toDouble())
        appDatabaseViewModel.updateRecord(recordEntity,weightEntity)
    }

    private fun deleteRecord() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(this.resources.getString(R.string.Yes)) {_, _ ->
            appDatabaseViewModel.deleteWeightRecord(args.selectedRecord?.id)
            args.selectedRecord?.let { appDatabaseViewModel.deleteRecord(it) }
            findNavController().popBackStack()
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
            R.style.WeightTheme
        )
        return inflater.cloneInContext(contextThemeWrapper)
    }
}