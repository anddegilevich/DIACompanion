package com.almazov.diacompanion.recipe

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import com.almazov.diacompanion.R
import com.almazov.diacompanion.meal.SelectWeightDialog

class SelectWeightRecipe(context: Context) : SelectWeightDialog(context) {
    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.Theme_DIACompanion)
        return inflater.cloneInContext(contextThemeWrapper)
    }
}