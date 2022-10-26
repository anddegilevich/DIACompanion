package com.almazov.diacompanion.add_record

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.editTextSeekBarSetup
import com.almazov.diacompanion.base.timeDateSelectSetup
import kotlinx.android.synthetic.main.fragment_insulin_add_record.btn_save
import kotlinx.android.synthetic.main.fragment_insulin_add_record.tv_Date
import kotlinx.android.synthetic.main.fragment_insulin_add_record.tv_Time
import kotlinx.android.synthetic.main.fragment_weight_add_record.*

class WeightAddRecord : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weight_add_record, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weightMin = 1
        val weightMax = 150

        editTextSeekBarSetup(weightMin, weightMax, edit_text_weight, seek_bar_weight)

        timeDateSelectSetup(childFragmentManager, tv_Time, tv_Date)

        btn_save.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_weightAddRecord_to_homePage)
        }
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(),
            R.style.WeightTheme
        )
        return inflater.cloneInContext(contextThemeWrapper)
    }
}