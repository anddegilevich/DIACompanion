package com.almazov.diacompanion.settings

import InfoAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.fragment_settings_help.view.*

class SettingsHelp : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings_help, container, false)

        infoList.clear()
        populateCategories()

        view.recycler_view_info_blocks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = InfoAdapter(infoList)
        }

        return view
    }

    private fun populateCategories() {

        val infoBlocks = arrayListOf(InfoBlock("О приложении",0),
            InfoBlock("1.1 Гестационный сахарный диабет (ГСД)",1),
            InfoBlock("1.2 Принципы питания",2),
            InfoBlock("1.3 Пищевые волокна",3),
            InfoBlock("1.4 Измерение глюкозы",4),
            InfoBlock("1.5 Физическая активность",5),
            InfoBlock("1.6 Гликемический индекс продуктов",6)
        )
        infoList.addAll(infoBlocks)

    }

}