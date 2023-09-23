package com.almazov.diacompanion.onboard.pages

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.fragment_setup_complete_page.view.*


class SetupCompletePage : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setup_complete_page, container, false)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val appType = sharedPreferences.getString("APP_TYPE", "GDMRCT")!!

        view.btn_finish.setOnClickListener {
            onBoardingFinish()
            val destination  = if (appType == "GDMRCT" || appType == "GDM") {
                SetupCompletePageDirections.actionSetupCompletePageToQuestionnaireFragment()
            } else {
                SetupCompletePageDirections.actionSetupCompletePageToHomePage()
            }
            findNavController().navigate(destination)
        }

        return view
    }

    private fun onBoardingFinish(){
        val finished = true
        sharedPreferences.edit().putBoolean("ON_BOARDING_FINISHED", finished).apply()
    }

}