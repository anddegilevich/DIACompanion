package com.almazov.diacompanion.home

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.RecordEntity
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_home_page.view.*
import kotlinx.android.synthetic.main.record_card.view.*


class HomePage : Fragment(), InterfaceRecordsInfo {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private lateinit var adapterRecords: HomeRecordsAdapter
    private var mBundleRecyclerViewState = Bundle()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onPause() {
        mBundleRecyclerViewState = Bundle()
        val mListState = record_recycler_view.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState.putParcelable("KEY_RECYCLER_STATE", mListState)
        super.onPause()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        adapterRecords = HomeRecordsAdapter(this)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        record_recycler_view.apply {
            adapter = adapterRecords
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        val appType = sharedPreferences.getString("APP_TYPE","")!!
        val name = sharedPreferences.getString("NAME","")!!

        tv_name.text = name

        appDatabaseViewModel.readLastRecords(appType).observe(viewLifecycleOwner, Observer { records ->
            if (records.isNullOrEmpty()) {
                tv_no_records.isVisible = true
            } else adapterRecords.setData(records)
            if (mBundleRecyclerViewState != null) {
                val mListState : Parcelable? = mBundleRecyclerViewState.getParcelable("KEY_RECYCLER_STATE")
                record_recycler_view.layoutManager?.onRestoreInstanceState(mListState)
            }
            startPostponedEnterTransition()
        })


        btn_add_record.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_recordsCategories)
        }

        btn_recipe_list.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_recipeList)
        }

        btn_export.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_exportData)
        }

        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        btn_options_group.setOnClickListener{
            drawer_layout.openDrawer(GravityCompat.START)
            //drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }

        nav_view.itemIconTintList = null
        nav_view.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_view_account -> {findNavController().navigate(R.id.action_homePage_to_settingsAccount)}
                R.id.nav_view_app_type -> {findNavController().navigate(R.id.action_homePage_to_settingsAppType)}
//                R.id.nav_view_notifications -> {findNavController().navigate(R.id.action_homePage_to_settingsNotifications)}
                R.id.nav_view_help -> {findNavController().navigate(R.id.action_homePage_to_settingsHelp)}
            }

            drawer_layout.closeDrawer(GravityCompat.START)
            true

        }

        recordHistoryLink.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_recordHistory)
        }

        ifOnBoardingFinished(view)
    }

    private fun ifOnBoardingFinished(view: View){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val finished: Boolean = sharedPreferences!!.getBoolean("ON_BOARDING_FINISHED", false)
        if (!finished) {Navigation.findNavController(view).navigate(R.id.action_homePage_to_greetingsPage)}
    }

    private fun deleteAllRecords(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(this.resources.getString(R.string.Yes)) { _, _ ->
             appDatabaseViewModel.deleteAllRecords()}
        builder.setNegativeButton(this.resources.getString(R.string.No)) { _, _ ->
        }
        builder.setTitle(this.resources.getString(R.string.DeleteAllRecords))
        builder.setMessage(this.resources.getString(R.string.AreUSureDeleteAllRecords))
        builder.create().show()
    }

    override fun transitionToRecordInfo(view: View, record: RecordEntity) {

        val destination = when (record.category) {
            "sugar_level_table" -> {HomePageDirections.actionHomePageToSugarLevelRecordInfo(record)}

            "insulin_table" -> {HomePageDirections.actionHomePageToInsulinRecordInfo(record)}

            "meal_table" -> {HomePageDirections.actionHomePageToMealRecordInfo(record)}

            "workout_table" -> {HomePageDirections.actionHomePageToWorkoutRecordInfo(record)}

            "sleep_table" -> {HomePageDirections.actionHomePageToSleepRecordInfo(record)}

            "weight_table" -> {HomePageDirections.actionHomePageToWeightRecordInfo(record)}

            "ketone_table" -> {HomePageDirections.actionHomePageToKetoneRecordInfo(record)}
            else -> {null}
        }
            val extras = FragmentNavigatorExtras(
                view.card_view to "card_view_info",
                view.img_category to "img_category_info"
            )
            findNavController().navigate(destination!!, extras)
    }

}
