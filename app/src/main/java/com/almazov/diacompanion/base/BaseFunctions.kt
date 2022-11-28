package com.almazov.diacompanion.base

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.meal.FoodInMealItem
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
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
fun timeDateSelectSetup(fragmentManager: FragmentManager, tvTime: TextView, tvDate: TextView): Long {
    val now = LocalDateTime.now()
    val dateSubmit = now.toEpochSecond(ZoneOffset.UTC)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    tvTime.text = now.format(timeFormatter)
    tvDate.text = now.format(dateFormatter)

    tvTime.setOnClickListener{
        openTimePicker(fragmentManager, tvTime)
    }

    tvDate.setOnClickListener{
        openDatePicker(fragmentManager, tvDate)
    }

    return dateSubmit
}

@SuppressLint("SimpleDateFormat")
@RequiresApi(Build.VERSION_CODES.O)
private fun openDatePicker(fragmentManager: FragmentManager, tvDate: TextView) {

    val myFormat = "dd.MM.yyyy"
    val date = tvDate.text.toString()

    val sPV = SimpleDateFormat(myFormat)
    val curDate = sPV.parse(date)
    val timeInMillis = curDate.time

    val picker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.SelectRecordDate)
            .setSelection(timeInMillis)
            .build()


    picker.show(fragmentManager, "DATE")

    picker.addOnPositiveButtonClickListener {
        tvDate.text = sPV.format(picker.selection)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun openTimePicker(fragmentManager: FragmentManager, tvTime: TextView){
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val now = LocalTime.parse(tvTime.text, timeFormatter)
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

fun convertDateToMils(date: String): Long {
    val formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")
    val localDate = LocalDateTime.parse(date, formatter)
    return localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
}

fun slideView(
    view: View,
    currentHeight: Int,
    newHeight: Int
) {
    val slideAnimator = ValueAnimator
        .ofInt(currentHeight, newHeight)
        .setDuration(200)

        slideAnimator.addUpdateListener { animation1: ValueAnimator ->
        val value = animation1.animatedValue as Int
        view.layoutParams.height = value
        view.requestLayout()
    }

    val animationSet = AnimatorSet()
    animationSet.interpolator = AccelerateDecelerateInterpolator()
    animationSet.play(slideAnimator)
    animationSet.start()
}

fun setTwoDigits(double: Double): Double{
    return BigDecimal(double).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
}

