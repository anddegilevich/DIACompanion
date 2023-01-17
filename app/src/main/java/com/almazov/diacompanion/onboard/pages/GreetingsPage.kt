package com.almazov.diacompanion.onboard.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.fragment_greetings_page.view.*

class GreetingsPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_greetings_page, container, false)

        view.btn_lets_start.setOnClickListener {
            findNavController().navigate(R.id.action_greetingsPage_to_settingsAccount)
        }
        return view
    }

}