package com.almazov.diacompanion.record_info

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.setTwoDigits
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_weight_record_info.*
import java.text.NumberFormat
import kotlin.math.pow

class WeightRecordInfo : Fragment() {

    private val args by navArgs<WeightRecordInfoArgs>()
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weight_record_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]
        appDatabaseViewModel.readWeightRecord(args.selectedRecord.id).observe(viewLifecycleOwner, Observer { record ->

            tv_weight.text = record.weight.toString()
//            showBMI(record.weight!!)
        })

        date.text = args.selectedRecord.date
        time.text = args.selectedRecord.time

        btn_edit.setOnClickListener{
            val action = WeightRecordInfoDirections.actionWeightRecordInfoToWeightAddRecord(args.selectedRecord)
            findNavController().navigate(action)
        }

        btn_delete.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton(this.resources.getString(R.string.Yes)) {_, _ ->
                appDatabaseViewModel.deleteWeightRecord(args.selectedRecord.id)
                args.selectedRecord.let { appDatabaseViewModel.deleteRecord(it) }
                findNavController().popBackStack()
            }
            builder.setNegativeButton(this.resources.getString(R.string.No)) {_, _ ->
            }
            builder.setTitle(this.resources.getString(R.string.DeleteRecord))
            builder.setMessage(this.resources.getString(R.string.AreUSureDeleteRecord))
            builder.create().show()
        }

        super.onViewCreated(view, savedInstanceState)

    }

    /*private fun showBMI(weight: Double) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val height = sharedPreferences.getFloat("HEIGHT", 1f)
        var bmi = setTwoDigits(weight / height.pow(2))
        tv_bmi.text = bmi.toString()
        if (bmi < 18.5) {
            tv_bmi_decryption.setText(R.string.BMILow)
            bmi = 18.5
        } else
        if (bmi < 25) tv_bmi_decryption.setText(R.string.BMINormal) else
        if (bmi < 30) tv_bmi_decryption.setText(R.string.BMIHigh) else
        if (bmi < 35) tv_bmi_decryption.setText(R.string.Obesity1) else
        if (bmi < 40) tv_bmi_decryption.setText(R.string.Obesity2) else {
            tv_bmi_decryption.setText(R.string.Obesity3)
            bmi = 40.0
        }

        slider_bmi.value = bmi.toFloat()
    }*/

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(),
            R.style.InsulinTheme
        )
        return inflater.cloneInContext(contextThemeWrapper)
    }

}