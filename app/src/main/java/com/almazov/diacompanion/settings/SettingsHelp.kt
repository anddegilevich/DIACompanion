package com.almazov.diacompanion.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.slideView
import kotlinx.android.synthetic.main.info_block_about_app.view.*
import kotlinx.android.synthetic.main.info_block_gsd.view.*


class SettingsHelp : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings_help, container, false)

        view.card_about_app.setOnClickListener{
            slideView(view.info_about_app)
        }

        view.card_gsd.setOnClickListener{
            slideView(view.info_gsd)
        }

        return view
    }

}