package com.almazov.diacompanion

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import com.almazov.diacompanion.base.editTextSeekBarSetup
import com.almazov.diacompanion.base.timeDateSelectSetup
import kotlinx.android.synthetic.main.fragment_insulin_add_record.*
import kotlinx.android.synthetic.main.fragment_insulin_add_record.btn_save
import kotlinx.android.synthetic.main.fragment_insulin_add_record.tv_Date
import kotlinx.android.synthetic.main.fragment_insulin_add_record.tv_Time
import kotlinx.android.synthetic.main.fragment_sleep_add_record.*
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.*

class SleepAddRecord : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_add_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sleepMin = 1
        val sleepMax = 12

        editTextSeekBarSetup(sleepMin, sleepMax, edit_text_sleep, seek_bar_sleep)

        timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        btn_save.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_sleepAddRecord_to_homePage)
        }
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.SleepTheme)
        return inflater.cloneInContext(contextThemeWrapper)
    }

}