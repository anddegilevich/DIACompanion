package com.almazov.diacompanion.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.fragment_settings_app_type.view.*

class SettingsAppType : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val view = inflater.inflate(R.layout.fragment_settings_app_type, container, false)

        view.btn_GDMRCT.setOnClickListener {
            changeAppType("GDMRCT")
        }

        view.btn_GDM.setOnClickListener {
            changeAppType("GDM")
        }

        view.btn_MS.setOnClickListener {
            changeAppType("MS")
        }

        view.btn_PCOS.setOnClickListener {
            changeAppType("PCOS")
        }

        return view
    }

    private fun changeAppType(appType: String) {
        sharedPreferences.edit().apply{
            putString("APP_TYPE",appType)
        }?.apply()
        findNavController().popBackStack()
    }

}