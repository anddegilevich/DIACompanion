package com.almazov.diacompanion.questionnaire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.almazov.diacompanion.R
import com.almazov.diacompanion.databinding.FragmentHealthQuestionnaireBinding
import com.almazov.diacompanion.questionnaire.models.YesNo
import com.almazov.diacompanion.questionnaire.models.YesNoDontKnow

class QuestionnaireHealthFragment : Fragment() {

    private val args by navArgs<QuestionnaireHealthFragmentArgs>()
    private var _binding: FragmentHealthQuestionnaireBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        }
    }

    private fun onContinueClick() {
        with(binding) {
            val data = args.data.apply {
                diabetesRelative = spinnerDiabetesRelative.selectedItem.toString()
                glucoseTolerance = spinnerGlucoseTolerance.selectedItem.toString()

                hypertensionBefore = spinnerHypertensionBefore.selectedItem.toString()
                hypertension = spinnerHypertension.selectedItem.toString()

                smoking6MonthBefore = spinnerSmoking6MonthBefore.selectedItem.toString()
                smokingBefore = spinnerSmokingBefore.selectedItem.toString()
                smoking = spinnerSmoking.selectedItem.toString()
            }

            findNavController().navigate(
                QuestionnaireHealthFragmentDirections.actionQuestionnaireHealthFragmentToQuestionnaireFoodFragment(
                    data
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}