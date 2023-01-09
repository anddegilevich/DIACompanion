package com.almazov.diacompanion.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.preference.PreferenceManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.openDatePicker
import kotlinx.android.synthetic.main.fragment_settings_account.*
import kotlinx.android.synthetic.main.fragment_settings_account.view.*
import kotlin.math.pow

class SettingsAccount : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var name: String
    private lateinit var secondName: String
    private lateinit var patronymic: String
    private lateinit var birthDate: String
    private var weigth = 0f
    private var heigth = 0f
    private var phone: Long = 0
    private lateinit var email: String
    private lateinit var attendingDoctor: String

    override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View? {
    // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings_account, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        view.spinnerDoctor.adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.AttendingDoctors,
            R.layout.spinner_item
        )

        view.tv_birth_date.setOnClickListener {
            openDatePicker(requireFragmentManager(), tv_birth_date)
        }

        name = sharedPreferences.getString("NAME","")!!
        secondName = sharedPreferences.getString("SECOND_NAME","")!!
        patronymic = sharedPreferences.getString("PATRONYMIC","")!!
        birthDate = sharedPreferences.getString("BIRTH_DATE","0")!!
        weigth = sharedPreferences.getFloat("WEIGHT",0f)
        heigth = sharedPreferences.getFloat("HEIGHT",0f)
        phone = sharedPreferences.getLong("PHONE",0)
        email = sharedPreferences.getString("EMAIL","")!!
        attendingDoctor = sharedPreferences.getString("ATTENDING_DOCTOR","")!!

        view.editTextFirstName.setText(name)
        view.editTextLastName.setText(secondName)
        view.editTextPatronymic.setText(patronymic)

        view.tv_birth_date.text = birthDate

        view.editTextWeight.setText(weigth.toString())
        view.editTextHeight.setText(heigth.toString())
        view.editTextPhone.setText(phone.toString())
        view.editTextEmail.setText(email)

        view.spinnerDoctor.setSelection(resources.getStringArray(R.array.AttendingDoctors).indexOf(attendingDoctor))

    return view
    }

    override fun onPause() {
        name = editTextFirstName.text.toString()
        secondName = editTextLastName.text.toString()
        patronymic = editTextPatronymic.text.toString()
        birthDate = tv_birth_date.text.toString()

        weigth = editTextWeight.text.toString().toFloat()
        heigth = editTextHeight.text.toString().toFloat()
        val bmi  = weigth/heigth.pow(2)
        phone = editTextPhone.text.toString().toLong()
        email = editTextEmail.text.toString()
        attendingDoctor = spinnerDoctor.selectedItem.toString()

        sharedPreferences.edit().apply{
            putString("NAME",name)
            putString("SECOND_NAME",secondName)
            putString("PATRONYMIC",patronymic)
            putString("BIRTH_DATE",birthDate)
            putFloat("WEIGHT",weigth)
            putFloat("HEIGHT",heigth)
            putLong("PHONE",phone)
            putString("EMAIL",email)
            putFloat("BMI",bmi)
            putString("ATTENDING_DOCTOR",attendingDoctor)
        }?.apply()
        super.onPause()
    }

}