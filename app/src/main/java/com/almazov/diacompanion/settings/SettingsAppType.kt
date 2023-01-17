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
import kotlinx.android.synthetic.main.fragment_settings_account.view.*
import kotlinx.android.synthetic.main.fragment_settings_app_type.view.*
import kotlinx.android.synthetic.main.fragment_settings_app_type.view.btn_save

class SettingsAppType : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var appType: String = "PCOS"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val view = inflater.inflate(R.layout.fragment_settings_app_type, container, false)

        val finished: Boolean = sharedPreferences.getBoolean("ON_BOARDING_FINISHED", false)
        if (finished) {
            view.btn_save.setOnClickListener {
                saveChanges()
                findNavController().popBackStack()
            }
        } else {
            view.btn_save.setOnClickListener {
                saveChanges()
                findNavController().navigate(R.id.action_settingsAppType_to_setupCompletePage)
            }
        }

        view.btn_GDMRCT.setOnClickListener {
            appType = "GDMRCT"
        }

        view.btn_GDM.setOnClickListener {
            appType = "GDM"
        }

        view.btn_MS.setOnClickListener {
            appType = "MS"
        }

        view.btn_PCOS.setOnClickListener {
            appType = "PCOS"
        }

        return view
    }

    private fun saveChanges() {
        sharedPreferences.edit().apply{
            putString("APP_TYPE",appType)
        }?.apply()
    }

}