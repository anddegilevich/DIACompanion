package com.almazov.diacompanion.base

import android.annotation.SuppressLint
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import com.almazov.diacompanion.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun editTextSeekBarSetup(min: Int, max: Int, editText: EditText, seekBar: SeekBar) {

    editText.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(string: Editable) {
            if (string.toString() != "") seekBar.progress =
                string.toString().toBigDecimal().toInt()
        }

        override fun beforeTextChanged(string: CharSequence, start: Int,
                                       count: Int, after: Int) {
        }

        override fun onTextChanged(string: CharSequence, start: Int,
                                   before: Int, count: Int) {
            if (string.toString() != "") {
                if (string.toString().toBigDecimal().toInt() > max) {
                    editText.setText(max.toString())
                    editText.setSelection(editText.length())
                }

                if (string.toString().toBigDecimal().toInt() < min) {
                    editText.setText(min.toString())
                    editText.setSelection(editText.length())
                }
            }
        }
    })

    seekBar.max = max
    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) editText.setText(progress.toString())
            editText.setSelection(editText.length())
        }

        override fun onStartTrackingTouch(seekbar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekbar: SeekBar?) {

        }
    })
}

@RequiresApi(Build.VERSION_CODES.O)
fun timeDateSelectSetup(fragmentManager: FragmentManager, tvTime: TextView, tvDate: TextView) {
    val now = LocalDateTime.now()
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    val time = now.format(timeFormatter)
    val date = now.format(dateFormatter)
    tvTime.text = time
    tvDate.text = date


    tvTime.setOnClickListener{
        openTimePicker(fragmentManager,time, tvTime)
    }

    tvDate.setOnClickListener{
        openDatePicker(fragmentManager,date, tvDate)
    }

}

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
private fun openDatePicker(fragmentManager: FragmentManager, date: String, tvDate: TextView) {

    val myFormat = "dd MMMM yyyy"

    val sdf = SimpleDateFormat(myFormat)
    val curDate = sdf.parse(date)
    val timeInMillis = curDate.time

    val picker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.SelectRecordDate)
            .setSelection(timeInMillis)
            .build()


    picker.show(fragmentManager, "DATE")

    picker.addOnPositiveButtonClickListener {
        tvDate.text = sdf.format(picker.selection)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun openTimePicker(fragmentManager: FragmentManager, time: String, tvTime: TextView){
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val now = LocalTime.parse(time, timeFormatter)
    val hourFormatter = DateTimeFormatter.ofPattern("HH")
    val minuteFormatter = DateTimeFormatter.ofPattern("mm")

    val currentHour = now.format(hourFormatter)
    val currentMinute = now.format(minuteFormatter)

    val picker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .setHour(currentHour.toInt())
        .setMinute(currentMinute.toInt())
        .setTitleText(R.string.SelectRecordTime)
        .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
        .build()
    picker.show(fragmentManager, "TIME")

    picker.addOnPositiveButtonClickListener{

        var selectedHour = if (picker.hour<10) {
            "0"+picker.hour.toString()
        } else {
            picker.hour.toString()
        }

        var selectedMinute = if (picker.minute<10) {
            "0"+picker.minute.toString()
        } else {
            picker.minute.toString()
        }

        val selectedTime = "$selectedHour:$selectedMinute"
        tvTime.text = selectedTime
    }
}