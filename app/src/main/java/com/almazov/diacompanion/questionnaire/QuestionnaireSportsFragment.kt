package com.almazov.diacompanion.questionnaire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.databinding.FragmentSportsQuestionnaireBinding
import com.almazov.diacompanion.questionnaire.models.SportTimes
import com.almazov.diacompanion.questionnaire.models.SteppingTimes
import com.almazov.diacompanion.questionnaire.models.WalkingTime

class QuestionnaireSportsFragment : Fragment() {

    private val args by navArgs<QuestionnaireSportsFragmentArgs>()
    private var _binding: FragmentSportsQuestionnaireBinding? = null
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]
        _binding = FragmentSportsQuestionnaireBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            spinnerWalkingBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                WalkingTime.values().map { it.text })
            spinnerWalking.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                WalkingTime.values().map { it.text })

            spinnerSteppingBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SteppingTimes.values().map { it.text })
            spinnerStepping.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SteppingTimes.values().map { it.text })

            spinnerSteppingBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SteppingTimes.values().map { it.text })
            spinnerStepping.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SteppingTimes.values().map { it.text })

            spinnerSportBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SportTimes.values().map { it.text })
            spinnerSport.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SportTimes.values().map { it.text })

            btnContinue.setOnClickListener { onContinueClick() }
        }
    }

    private fun onContinueClick() {
        with(binding) {
            val data = args.data.apply {
                id = 0
                walkingBefore = spinnerWalkingBefore.selectedItem.toString()
                walking= spinnerWalking.selectedItem.toString()
                steppingBefore = spinnerSteppingBefore.selectedItem.toString()
                stepping = spinnerStepping.selectedItem.toString()
                sportBefore = spinnerSportBefore.selectedItem.toString()
                sport = spinnerSport.selectedItem.toString()
            }
            appDatabaseViewModel.saveQuestionnaire(data)

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            sharedPreferences.edit().putBoolean("QUESTIONNARIE_FINISHED", true).apply()

            findNavController().navigate(QuestionnaireSportsFragmentDirections.actionQuestionnaireSportsFragmentToHomePage())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}