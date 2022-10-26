package com.almazov.diacompanion

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_home_page.*

class HomePage : Fragment() {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ifOnBoardingFinished(view)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        btn_add_record.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_recordsCategories)
        }

        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        btn_options_group.setOnClickListener{
            drawer_layout.openDrawer(GravityCompat.START)
            //drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }

        nav_view.itemIconTintList = null
        nav_view.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_view_account -> {deleteAllRecords()}
                R.id.nav_view_app_type -> { }
            }

            drawer_layout.closeDrawer(GravityCompat.START)
            true

        }

        recordHistoryLink.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_recordHistory)
        }

    }

    private fun ifOnBoardingFinished(view: View){
        val sharedPreferences = context?.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        val Finished: Boolean = sharedPreferences!!.getBoolean("ON_BOARDING_FINISHED", false)
        if (!Finished) {Navigation.findNavController(view).navigate(R.id.action_homePage_to_onBoardingViewPager)}
    }

    private fun deleteAllRecords(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(this.resources.getString(R.string.Yes)) {_, _ ->
             appDatabaseViewModel.deleteAllRecords()}
        builder.setNegativeButton(this.resources.getString(R.string.No)) {_, _ ->
        }
        builder.setTitle(this.resources.getString(R.string.DeleteAllRecords))
        builder.setMessage(this.resources.getString(R.string.AreUSureDeleteAllRecords))
        builder.create().show()
    }

}
