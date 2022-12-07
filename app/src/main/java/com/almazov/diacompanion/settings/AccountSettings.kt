package com.almazov.diacompanion.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.almazov.diacompanion.R


class AccountSettings : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.account_settings, rootKey)

        val prefName: EditTextPreference? = findPreference("NAME")
        prefName!!.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE or
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }

        val prefSecondName: EditTextPreference? = findPreference("SECOND_NAME")
        prefSecondName!!.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }
        val prefPatronymic: EditTextPreference? = findPreference("PATRONYMIC")
        prefPatronymic!!.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }

        /*val prefHeight: EditTextPreference? = findPreference("HEIGHT")
        prefHeight!!.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        }*/
    }

}