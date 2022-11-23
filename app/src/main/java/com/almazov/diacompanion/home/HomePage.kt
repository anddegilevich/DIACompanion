package com.almazov.diacompanion.home

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.RecordEntity
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_home_page.view.*
import kotlinx.android.synthetic.main.record_card.view.*
import kotlinx.android.synthetic.main.record_card.view.card_view
import kotlinx.android.synthetic.main.record_card.view.img_category
import kotlinx.android.synthetic.main.record_card.view.main_info


class HomePage : Fragment(), InterfaceRecordsInfo {

    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private lateinit var adapterRecords: HomeRecordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedElementReturnTransition = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]

        adapterRecords = HomeRecordsAdapter(this)

        view.record_recycler_view.apply {
            this.adapter = adapterRecords
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            postponeEnterTransition()
            viewTreeObserver
                .addOnPreDrawListener {
                    true
                }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabaseViewModel.readLastRecords().observe(viewLifecycleOwner, Observer { records ->
            if (records.isNullOrEmpty()) {
                tv_no_records.isVisible = true
            } else adapterRecords.setData(records)
            (view.parent as? ViewGroup)?.doOnPreDraw {

                view.doOnPreDraw { startPostponedEnterTransition() }
            }
        })

        btn_add_record.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_recordsCategories)
        }

        btn_recipe_list.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_recipeList)
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
                R.id.nav_view_app_type -> {/*Navigation.findNavController(view).navigate(R.id.action_homePage_to_mealList)*/ }
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
        val sharedPreferences = context?.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        val finished: Boolean = sharedPreferences!!.getBoolean("ON_BOARDING_FINISHED", false)
        if (!finished) {Navigation.findNavController(view).navigate(R.id.action_homePage_to_onBoardingViewPager)}
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
        val destination = HomePageDirections.actionHomePageToMealRecordInfo(record)
        val extras = FragmentNavigatorExtras(view.card_view to "card_view_info",
        view.img_category to "img_category_info", view.main_info to "main_info_info",
        view.date to "date_info", view.time to "time_info")
        findNavController().navigate(destination, extras)
    }

}
