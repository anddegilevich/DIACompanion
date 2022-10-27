package com.almazov.diacompanion.add_record

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.editTextSeekBarSetup
import com.almazov.diacompanion.base.timeDateSelectSetup
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.RecordEntity
import com.almazov.diacompanion.data.SugarLevelEntity
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.*
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.btn_save
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.tv_Date
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.tv_Time


class SugarLevelAddRecord : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var dateSubmit: String? = null
    var UpdateBool: Boolean = false
    private val args by navArgs<SugarLevelAddRecordArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        UpdateBool = args.selectedRecord != null
        return inflater.inflate(R.layout.fragment_sugar_level_add_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        val sugarLevelMin = 1
        val sugarLevelMax = 20


        editTextSeekBarSetup(sugarLevelMin, sugarLevelMax, edit_text_sugar_level, seek_bar_sugar_level)

        spinner_sugar_level.adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.SugarLevelSpinner,
            R.layout.spinner_item
        )

        timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        if (UpdateBool)
        {
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
                if (UpdateBool) updateRecord() else addRecord()
                Navigation.findNavController(view).navigate(R.id.action_sugarLevelAddRecord_to_homePage)
            }

        }
    }

    private fun addRecord(){
        val category = "sugar_level_table"
        val sugarLevel = edit_text_sugar_level.text.toString()
        val mainInfo = sugarLevel + " " +
                this.resources.getString(R.string.mmoll)
        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()

        val sugarLevelEntity = SugarLevelEntity(null,sugarLevel.toDouble(),null,null)

        appDatabaseViewModel.addRecord(sugarLevelEntity)


        val recordEntity = RecordEntity(null, category, sugarLevelEntity.id, mainInfo, time, date,
            dateSubmit,false)

        appDatabaseViewModel.addRecord(recordEntity)

    }

    private fun updateRecord(){
        val sugarLevel = edit_text_sugar_level.text.toString()
        val mainInfo = sugarLevel + " " +
                this.resources.getString(R.string.mmoll)
        val time = tv_Time.text.toString()
        val date = tv_Date.text.toString()

        val recordEntity = RecordEntity(args.selectedRecord?.id, args.selectedRecord?.category, null, mainInfo, time, date,
            args.selectedRecord?.dateSubmit,false)
        appDatabaseViewModel.updateRecord(recordEntity)
    }

    private fun deleteRecord() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(this.resources.getString(R.string.Yes)) {_, _ ->
            args.selectedRecord?.let { appDatabaseViewModel.deleteRecord(it) }
            findNavController().navigate(R.id.action_sugarLevelAddRecord_to_homePage)
        }
        builder.setNegativeButton(this.resources.getString(R.string.No)) {_, _ ->
        }
        builder.setTitle(this.resources.getString(R.string.DeleteRecord))
        builder.setMessage(this.resources.getString(R.string.AreUSureDeleteRecord))
        builder.create().show()
    }

}