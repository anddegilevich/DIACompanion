package com.almazov.diacompanion.onboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.almazov.diacompanion.R
import com.almazov.diacompanion.onboard.pages.GreetingsPage
import com.almazov.diacompanion.onboard.pages.SetupCompletePage
import com.almazov.diacompanion.onboard.pages.SetupPage1
import kotlinx.android.synthetic.main.fragment_on_boarding_view_pager.view.*

class OnBoardingViewPager : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_on_boarding_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            GreetingsPage(),
            SetupPage1(),
            SetupCompletePage()
        )

        val adapter = ViewPagerAdapter(
            requireActivity().supportFragmentManager,
            lifecycle,
            fragmentList
        )

        view.ViewPager.adapter = adapter
        return view
    }

}