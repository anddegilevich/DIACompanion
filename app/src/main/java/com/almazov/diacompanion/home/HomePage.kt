package com.almazov.diacompanion.home

import android.content.SharedPreferences
import android.os.Bundle
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.RecordEntity
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.present_day_info_card.*
import kotlinx.android.synthetic.main.record_card.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


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

        ifOnBoardingFinished()

        postponeEnterTransition()
        record_recycler_view.apply {
            adapter = adapterRecords
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        val appType = sharedPreferences.getString("APP_TYPE", "")!!

        appDatabaseViewModel.readLastRecords(appType)
            .observe(viewLifecycleOwner, Observer { records ->
                if (records.isNullOrEmpty()) {
                    tv_no_records.isVisible = true
                } else adapterRecords.setData(records)
                if (mBundleRecyclerViewState != null) {
                    val mListState: Parcelable? =
                        mBundleRecyclerViewState.getParcelable("KEY_RECYCLER_STATE")
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

        btn_options_group.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
            //drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }

        nav_view.itemIconTintList = null
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_view_account -> {
                    findNavController().navigate(R.id.action_homePage_to_settingsAccount)
                }

                R.id.nav_view_app_type -> {
                    findNavController().navigate(R.id.action_homePage_to_settingsAppType)
                }

                R.id.nav_view_help -> {
                    findNavController().navigate(R.id.action_homePage_to_settingsHelp)
                }

                R.id.nav_view_questions -> {
                    findNavController().navigate(R.id.action_homePage_to_questionnaireFragment)
                }

                else -> Unit
            }

            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
        if (appType == "GDM" || appType == "GDMRCT") {
            nav_view.inflateMenu(R.menu.nav_menu_with_questions)
        }

        recordHistoryLink.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homePage_to_recordHistory)
        }

    }

    private fun ifOnBoardingFinished() {
        val finished: Boolean = sharedPreferences.getBoolean("ON_BOARDING_FINISHED", false)
        val questionnaireFinished: Boolean = sharedPreferences.getBoolean("QUESTIONNARIE_FINISHED", false)
        val appType = sharedPreferences.getString("APP_TYPE", "GDMRCT")!!

        when {
            !finished -> {
                findNavController().navigate(R.id.action_homePage_to_greetingsPage)
            }
            !questionnaireFinished && (appType == "GDMRCT" || appType == "GDM") -> {
               findNavController().navigate(R.id.action_homePage_to_questionnaireFragment)
            }
            else -> {
                val name = sharedPreferences.getString("NAME", "")!!
                val secondName = sharedPreferences.getString("SECOND_NAME", "")!!
                val patronymic = sharedPreferences.getString("PATRONYMIC", "")!!

                tv_name.text = secondName + " " + name[0] + ". " + patronymic[0] + "."

                val now = LocalDateTime.now()
                val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale("ru"))
                val presentDate = now.format(dateFormatter)
                tv_present_date.text = presentDate

                GlobalScope.launch(Dispatchers.Main) {
                    val presentDayMealRecords = GlobalScope.async(Dispatchers.Default) {
                        appDatabaseViewModel.readPresentDayMealRecords(presentDate)
                    }

                    val mealList = presentDayMealRecords.await()
                    var proteins = 0.0
                    var fats = 0.0
                    var carbs = 0.0
                    var kkals = 0.0
                    for (meal in mealList) {
                        for (food in meal.mealWithFoods.foods) {
                            val weight = food.foodInMealEntity.weight!! / 100
                            proteins += weight * food.food.prot!!
                            fats += weight * food.food.fat!!
                            carbs += weight * food.food.carbo!!
                            kkals += weight * food.food.ec!!
                        }
                    }
                    tv_protein.text = proteins.toInt().toString()
                    tv_fat.text = fats.toInt().toString()
                    tv_carbs.text = carbs.toInt().toString()
                    tv_kkal.text = kkals.toInt().toString()
                }
            }
        }
    }

    override fun transitionToRecordInfo(view: View, record: RecordEntity) {

        val destination = when (record.category) {
            "sugar_level_table" -> {
                HomePageDirections.actionHomePageToSugarLevelRecordInfo(record)
            }

            "insulin_table" -> {
                HomePageDirections.actionHomePageToInsulinRecordInfo(record)
            }

            "meal_table" -> {
                HomePageDirections.actionHomePageToMealRecordInfo(record)
            }

            "workout_table" -> {
                HomePageDirections.actionHomePageToWorkoutRecordInfo(record)
            }

            "sleep_table" -> {
                HomePageDirections.actionHomePageToSleepRecordInfo(record)
            }

            "weight_table" -> {
                HomePageDirections.actionHomePageToWeightRecordInfo(record)
            }

            "ketone_table" -> {
                HomePageDirections.actionHomePageToKetoneRecordInfo(record)
            }

            else -> {
                null
            }
        }
        val extras = FragmentNavigatorExtras(
            view.card_view to "card_view_info",
            view.img_category to "img_category_info"
        )
        findNavController().navigate(destination!!, extras)
    }

}
