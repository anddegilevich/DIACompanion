package com.almazov.diacompanion.onboard.pages

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.almazov.diacompanion.R
import com.almazov.diacompanion.onboard.OnBoardingViewPager
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_setup_complete_page.view.*
import kotlinx.android.synthetic.main.fragment_setup_page1.view.*
import kotlinx.android.synthetic.main.fragment_setup_page1.view.btn_back


class SetupCompletePage : Fragment() {

    private val pageNum: Int = 3;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setup_complete_page, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.ViewPager)

       view.btn_finish.setOnClickListener {
           OnBoardingFinish()
           Navigation.findNavController(view).navigate(R.id.action_onBoardingViewPager_to_homePage)
       }

        view.btn_back.setOnClickListener {
            viewPager?.currentItem = pageNum-1
        }

        return view
    }

    private fun OnBoardingFinish(){
        val Finished: Boolean = true

        val sharedPreferences = context?.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.apply{
            putBoolean("ON_BOARDING_FINISHED", Finished)
        }?.apply()

    }

}