package com.almazov.diacompanion.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.almazov.diacompanion.R

class AccountSettings : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}