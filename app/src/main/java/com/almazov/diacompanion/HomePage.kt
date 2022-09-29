package com.almazov.diacompanion

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_home_page.*

class HomePage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)
        ifOnBoardingFinished(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_add_record.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_recordsCategories)
        }
    }

    private fun ifOnBoardingFinished(view: View){
        val sharedPreferences = context?.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        val Finished: Boolean = sharedPreferences!!.getBoolean("ON_BOARDING_FINISHED", false)
        if (!Finished) {Navigation.findNavController(view).navigate(R.id.action_homePage_to_onBoardingViewPager)}
    }

}
