package com.almazov.diacompanion.meal

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.editTextSeekBarSetup
import kotlinx.android.synthetic.main.fragment_sugar_level_add_record.*
import kotlinx.android.synthetic.main.select_weight_dialog.view.*

open class SelectWeightDialog(context: Context, val weight: Double?): DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.select_weight_dialog, container, false)

        val weightMin = 1
        val weightMax = 500

        editTextSeekBarSetup(weightMin, weightMax, view.edit_text_weight, view.seek_bar_weight)

        view.btn_save.setOnClickListener{
            val weightSubmitted = !view.edit_text_weight.text.toString().isNullOrBlank()
            if (weightSubmitted) {
                val result = view.edit_text_weight.text.toString()
                setFragmentResult("requestKey", bundleOf("resultKey" to result))
                dismiss()
            }
        }

        if (dialog != null && dialog!!.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE);
        }

        if (weight != null) view.edit_text_weight.setText(weight.toString())

        return view
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.InsulinTheme)
        return inflater.cloneInContext(contextThemeWrapper)
    }

}