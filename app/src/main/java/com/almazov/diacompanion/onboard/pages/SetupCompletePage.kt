package com.almazov.diacompanion.onboard.pages

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.convertDateToMils
import kotlinx.android.synthetic.main.fragment_setup_complete_page.view.*
import kotlinx.android.synthetic.main.fragment_setup_page1.view.btn_back
import java.io.*
import kotlin.math.pow


class SetupCompletePage : Fragment() {

    private val pageNum: Int = 3;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setup_complete_page, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.ViewPager)

       view.btn_finish.setOnClickListener {
           onBoardingFinish()
           Navigation.findNavController(view).navigate(R.id.action_onBoardingViewPager_to_homePage)
       }

        view.btn_back.setOnClickListener {
            viewPager?.currentItem = pageNum-1
        }

        return view
    }

    private fun onBoardingFinish(){

        try {
            val myInput: InputStream = requireContext().assets.open("model.model")
            val modelPath: String = requireContext().getDatabasePath("model.model").path
            val myOutput: OutputStream = FileOutputStream(modelPath)
            val buffer = ByteArray(1024)
            var length: Int
            while (myInput.read(buffer).also { length = it } > 0) {
                myOutput.write(buffer, 0, length)
            }
            myOutput.flush()
            myOutput.close()
            myInput.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val finished = true
        val height  = 1.81f
        val weight  = 65f
        val bmi  = weight/height.pow(2)
        val pregnancyStartDate = "00:00 20.10.2022"
        val pregnancyStartDateLong = convertDateToMils(pregnancyStartDate)

        val name = "Имя"
        val secondName = "Фамилия"
        val patronymic = "Отчество"

        val attendingDoctor = "Лечащий врач"
        val birthDate = "00:00 01.01.2000"
        val birthDateLong = convertDateToMils(birthDate)

        val appType = 1


        val sharedPreferences = context?.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.apply{
            putBoolean("ON_BOARDING_FINISHED", finished)
            putFloat("HEIGHT",height)
            putFloat("BMI",bmi)
            putLong("PREGNANCY_START_DATE",pregnancyStartDateLong)
            putString("NAME",name)
            putString("SECOND_NAME",secondName)
            putString("PATRONYMIC",patronymic)
            putString("ATTENDING_DOCTOR",attendingDoctor)
            putLong("BIRTH_DATE",birthDateLong)
            putInt("APP_TYPE",appType)
        }?.apply()

    }

}