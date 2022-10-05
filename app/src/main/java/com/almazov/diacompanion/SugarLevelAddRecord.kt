package com.almazov.diacompanion

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.almazov.diacompanion.base.editTextSeekBarSetup
import com.almazov.diacompanion.base.timeDateSelectSetup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class SugarLevelAddRecord : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_sugar_level_add_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sugarLevelMin = 1
        val sugarLevelMax = 20

        editTextSeekBarSetup(sugarLevelMin, sugarLevelMax, edit_text_sugar_level, seek_bar_sugar_level)

        spinner_sugar_level.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.SugarLevelSpinner, R.layout.spinner_item)

        timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        btn_save.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_sugarLevelAddRecord_to_homePage)
        }

    }

}