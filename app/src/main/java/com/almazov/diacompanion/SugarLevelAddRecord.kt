package com.almazov.diacompanion

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.*
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

        val sugarLevelMin: Int = 1
        val sugarLevelMax: Int = 20

        edit_text_sugar_level.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.toString() != "") seek_bar_sugar_level.setProgress(s.toString().toBigDecimal().toInt())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (s.toString() != "") {
                    if (s.toString().toBigDecimal().toInt() > sugarLevelMax) {
                        edit_text_sugar_level.setText(sugarLevelMax.toString())
                        edit_text_sugar_level.setSelection(edit_text_sugar_level.length())
                    }

                    if (s.toString().toBigDecimal().toInt() < sugarLevelMin) {
                        edit_text_sugar_level.setText(sugarLevelMin.toString())
                        edit_text_sugar_level.setSelection(edit_text_sugar_level.length())
                    }
                }
            }
        })

        seek_bar_sugar_level.setMax(sugarLevelMax)
        seek_bar_sugar_level.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) edit_text_sugar_level.setText(progress.toString())
            }

            override fun onStartTrackingTouch(seekbar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {

            }
        })

        spinner_prefs.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.SugarLevelSpinner, R.layout.spinner_item)

        val now = LocalDateTime.now()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy")

        val time = now.format(timeFormatter)
        val date = now.format(dateFormatter)
        tv_Time.text = time
        tv_Date.text = date

        tv_Time.setOnClickListener{
            openTimePicker(tv_Time.text.toString())
        }
        
        tv_Date.setOnClickListener{
            openDatePicker(tv_Date.text.toString())
        }

    }

    private fun openDatePicker(date: String) {

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openTimePicker(time: String) {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        try {
            val now = LocalTime.parse(time, timeFormatter)
        } catch (e: java.time.format.DateTimeParseException){

            val time2 = "0$time"
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        }

        val now = LocalTime.parse(time, timeFormatter)
        val hourFormatter = DateTimeFormatter.ofPattern("HH")
        val minuteFormatter = DateTimeFormatter.ofPattern("mm")

        val currentHour = now.format(hourFormatter)
        val currentMinute = now.format(minuteFormatter)

        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(currentHour.toInt())
            .setMinute(currentMinute.toInt())
            .setTitleText(R.string.SelectRecordTime)
            .setInputMode(INPUT_MODE_KEYBOARD)
            .build()
        picker.show(childFragmentManager, "TIME")

        picker.addOnPositiveButtonClickListener{
            val selectedTime = picker.hour.toString() + ":" + picker.minute.toString()
            tv_Time.text = selectedTime
        }
    }


}