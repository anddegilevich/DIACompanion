package com.almazov.diacompanion.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.openDatePicker
import com.almazov.diacompanion.base.slideView
import kotlinx.android.synthetic.main.fragment_settings_account.*
import kotlinx.android.synthetic.main.fragment_settings_account.view.*
import kotlin.math.pow


class SettingsAccount : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var name: String
    private lateinit var secondName: String
    private lateinit var patronymic: String
    private lateinit var birthDate: String
    private lateinit var pregnancyDate: String
    private var weigth = 0f
    private var heigth = 0f
    private lateinit var phone: String
    private lateinit var email: String
    private lateinit var attendingDoctor: String
    private lateinit var apptype: String
    private var patientId = 0

    override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View? {
    // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings_account, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        apptype = sharedPreferences.getString("APP_TYPE","PCOS")!!

        view.spinnerDoctor.adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.AttendingDoctors,
            R.layout.spinner_item
        )

        view.tv_birth_date.setOnClickListener {
            openDatePicker(requireFragmentManager(), tv_birth_date)
        }
        if ((apptype == "GDMRCT") or (apptype == "GDM")){
            slideView(view.layout_pregnancy)
            view.tv_pregnancy_date.setOnClickListener {
                openDatePicker(requireFragmentManager(), tv_pregnancy_date)
            }
            view.tv_weight_kg.setText(R.string.WeightKgPregnancy)
        }

//        view.editTextPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        val finished: Boolean = sharedPreferences.getBoolean("ON_BOARDING_FINISHED", false)
        if (finished) {

            name = sharedPreferences.getString("NAME","")!!
            secondName = sharedPreferences.getString("SECOND_NAME","")!!
            patronymic = sharedPreferences.getString("PATRONYMIC","")!!
            birthDate = sharedPreferences.getString("BIRTH_DATE","01.01.2000")!!
            pregnancyDate = sharedPreferences.getString("PREGNANCY_DATE","01.01.2000")!!
            weigth = sharedPreferences.getFloat("WEIGHT_BEFORE_PREGNANCY",0f)
            heigth = sharedPreferences.getFloat("HEIGHT",0f)
            phone = sharedPreferences.getString("PHONE","")!!
            email = sharedPreferences.getString("EMAIL","")!!
            attendingDoctor = sharedPreferences.getString("ATTENDING_DOCTOR","")!!
            patientId = sharedPreferences.getInt("PATIENT_ID",1)

            view.editTextFirstName.setText(name)
            view.editTextLastName.setText(secondName)
            view.editTextPatronymic.setText(patronymic)

            view.tv_birth_date.text = birthDate
            view.tv_pregnancy_date.text = pregnancyDate

            view.editTextWeight.setText(weigth.toString())
            view.editTextHeight.setText(heigth.toString())
            /*view.editTextPhone.setText(phone)
            view.editTextEmail.setText(email)*/
            view.spinnerDoctor.setSelection(resources.getStringArray(R.array.AttendingDoctors).indexOf(attendingDoctor))
            view.editTextPatientId.setText(patientId.toString())

        }

        view.btn_save.setOnClickListener {
            if (fieldsAreFilled()) {
                applyChanges(finished)
                if (finished)
                    findNavController().popBackStack()
                else
                    findNavController().navigate(R.id.action_settingsAccount_to_setupCompletePage)

            } else
            {
                if (view.layout_notification.height == 0)
                    slideView(view.layout_notification)
            }
        }


    return view
    }

    private fun applyChanges(finished: Boolean) {
        name = editTextFirstName.text.toString()
        secondName = editTextLastName.text.toString()
        patronymic = editTextPatronymic.text.toString()
        birthDate = tv_birth_date.text.toString()
        pregnancyDate = tv_pregnancy_date.text.toString()

        weigth = editTextWeight.text.toString().toFloat()
        heigth = editTextHeight.text.toString().toFloat()
        val bmi  = weigth/heigth.pow(2)
        /*phone = editTextPhone.text.toString()
        email = editTextEmail.text.toString()*/
        attendingDoctor = spinnerDoctor.selectedItem.toString()
        patientId = editTextPatientId.text.toString().toInt()

            sharedPreferences.edit().apply{
            putString("NAME",name)
            putString("SECOND_NAME",secondName)
            putString("PATRONYMIC",patronymic)
            putString("BIRTH_DATE",birthDate)
            putFloat("WEIGHT_BEFORE_PREGNANCY",weigth)
            if (!finished) {
                val bmi  = weigth/heigth.pow(2)
                putFloat("WEIGHT",weigth)
                putFloat("BMI",bmi)
            }
            putFloat("HEIGHT",heigth)
            /*putString("PHONE",phone)
            putString("EMAIL",email)*/
            putString("ATTENDING_DOCTOR",attendingDoctor)
            putInt("PATIENT_ID",patientId)
            if ((apptype == "GDMRCT") or (apptype == "GDM")){
                putString("PREGNANCY_DATE",pregnancyDate)
            }
        }?.apply()
    }

    private fun fieldsAreFilled(): Boolean{
        return (!editTextFirstName.text.isEmpty() and !editTextLastName.text.isEmpty()
            and !editTextPatronymic.text.isEmpty() and !tv_birth_date.text.isEmpty()
            and !editTextWeight.text.isEmpty() and !editTextHeight.text.isEmpty()
            and !editTextPatientId.text.isEmpty())
    }

}