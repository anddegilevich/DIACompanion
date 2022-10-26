package com.almazov.diacompanion.onboard.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.almazov.diacompanion.R
import kotlinx.android.synthetic.main.fragment_setup_page1.view.*

class SetupPage2 : Fragment() {

    private val pageNum: Int = 2;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setup_page2, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.ViewPager)

        view.btn_next.setOnClickListener {
            viewPager?.currentItem = pageNum+1
        }

        view.btn_back.setOnClickListener {
            viewPager?.currentItem = pageNum-1
        }

        return view}

}