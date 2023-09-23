package com.almazov.diacompanion.questionnaire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.setSelectedByTitle
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.almazov.diacompanion.data.QuestionnaireEntity
import com.almazov.diacompanion.databinding.FragmentHealthQuestionnaireBinding
import com.almazov.diacompanion.questionnaire.models.YesNo
import com.almazov.diacompanion.questionnaire.models.YesNoDontKnow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class QuestionnaireHealthFragment : Fragment() {

    private var _binding: FragmentHealthQuestionnaireBinding? = null

    private val binding get() = _binding!!
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var data: QuestionnaireEntity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]
        _binding = FragmentHealthQuestionnaireBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            spinnerDiabetesRelative.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNoDontKnow.values().map { it.text })
            spinnerGlucoseTolerance.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNoDontKnow.values().map { it.text })
            spinnerHypertensionBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNoDontKnow.values().map { it.text })
            spinnerHypertension.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNoDontKnow.values().map { it.text })
            spinnerSmoking6MonthBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNo.values().map { it.text })
            spinnerSmokingBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNo.values().map { it.text })
            spinnerSmoking.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNo.values().map { it.text })
            btnContinue.setOnClickListener { onContinueClick() }
            initQuestionnaireData()
        }
    }

    private fun initQuestionnaireData() {
        CoroutineScope(Dispatchers.Main).launch {
            val questionnaireDeferred = CoroutineScope(Dispatchers.IO).async {
                appDatabaseViewModel.getQuestionnaire()
            }
            val questionnaire = questionnaireDeferred.await() ?: return@launch
            data = questionnaire
            with(questionnaire) {
                with(binding) {
                    spinnerDiabetesRelative.setSelectedByTitle(diabetesRelative)
                    spinnerGlucoseTolerance.setSelectedByTitle(glucoseTolerance)

                    spinnerHypertensionBefore.setSelectedByTitle(hypertensionBefore)
                    spinnerHypertension.setSelectedByTitle(hypertension)

                    spinnerSmoking6MonthBefore.setSelectedByTitle(smoking6MonthBefore)
                    spinnerSmokingBefore.setSelectedByTitle(smokingBefore)
                    spinnerSmoking.setSelectedByTitle(smoking)
                }
            }
        }
    }

    private fun onContinueClick() {
        with(binding) {
            data?.apply {
                diabetesRelative = spinnerDiabetesRelative.selectedItem.toString()
                glucoseTolerance = spinnerGlucoseTolerance.selectedItem.toString()

                hypertensionBefore = spinnerHypertensionBefore.selectedItem.toString()
                hypertension = spinnerHypertension.selectedItem.toString()

                smoking6MonthBefore = spinnerSmoking6MonthBefore.selectedItem.toString()
                smokingBefore = spinnerSmokingBefore.selectedItem.toString()
                smoking = spinnerSmoking.selectedItem.toString()
            }
            data?.let {
                appDatabaseViewModel.saveQuestionnaire(it)
            }
            findNavController().navigate(
                QuestionnaireHealthFragmentDirections.actionQuestionnaireHealthFragmentToQuestionnaireFoodFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}