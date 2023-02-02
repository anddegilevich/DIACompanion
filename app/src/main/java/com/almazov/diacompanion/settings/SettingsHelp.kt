package com.almazov.diacompanion.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.slideView
import kotlinx.android.synthetic.main.info_block_about_app.view.*
import kotlinx.android.synthetic.main.info_block_add_record.view.*
import kotlinx.android.synthetic.main.info_block_eating_habits.view.*
import kotlinx.android.synthetic.main.info_block_export.view.*
import kotlinx.android.synthetic.main.info_block_gi.view.*
import kotlinx.android.synthetic.main.info_block_gsd.view.*
import kotlinx.android.synthetic.main.info_block_help.view.*
import kotlinx.android.synthetic.main.info_block_home_page.view.*
import kotlinx.android.synthetic.main.info_block_meal_records.view.*
import kotlinx.android.synthetic.main.info_block_physical_act.view.*
import kotlinx.android.synthetic.main.info_block_pv.view.*
import kotlinx.android.synthetic.main.info_block_recipe.view.*
import kotlinx.android.synthetic.main.info_block_record_history.view.*
import kotlinx.android.synthetic.main.info_block_record_updates.view.*
import kotlinx.android.synthetic.main.info_block_settings.view.*
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

        view.card_home_page.setOnClickListener{
            slideView(view.info_home_page)
        }

        view.card_settings.setOnClickListener{
            slideView(view.info_settings)
        }

        view.card_add_record.setOnClickListener{
            slideView(view.info_add_record)
        }

        view.card_meal_records.setOnClickListener{
            slideView(view.info_meal_records)
        }

        view.card_record_history.setOnClickListener{
            slideView(view.info_record_history)
        }

        view.card_record_updates.setOnClickListener{
            slideView(view.info_record_updates)
        }

        view.card_recipe.setOnClickListener{
            slideView(view.info_recipe)
        }

        view.card_export.setOnClickListener{
            slideView(view.info_export)
        }

        return view
    }

}