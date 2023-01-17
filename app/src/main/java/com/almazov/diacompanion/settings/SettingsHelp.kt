package com.almazov.diacompanion.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.slideView
import kotlinx.android.synthetic.main.info_block_about_app.view.*
import kotlinx.android.synthetic.main.info_block_eating_habits.view.*
import kotlinx.android.synthetic.main.info_block_gi.view.*
import kotlinx.android.synthetic.main.info_block_gsd.view.*
import kotlinx.android.synthetic.main.info_block_help.view.*
import kotlinx.android.synthetic.main.info_block_physical_act.view.*
import kotlinx.android.synthetic.main.info_block_pv.view.*
import kotlinx.android.synthetic.main.info_block_sugar.view.*


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

        view.card_eating_habits.setOnClickListener{
            slideView(view.info_eating_habits)
        }

        view.card_pv.setOnClickListener{
            slideView(view.info_pv)
        }

        view.card_sugar.setOnClickListener{
            slideView(view.info_sugar)
        }

        view.card_physical_act.setOnClickListener{
            slideView(view.info_physical_act)
        }

        view.card_gi.setOnClickListener{
            slideView(view.info_gi)
        }

        view.card_help.setOnClickListener{
            slideView(view.info_help)
        }

        return view
    }

}