package com.almazov.diacompanion.questionnaire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.QuestionnaireEntity
import com.almazov.diacompanion.databinding.FragmentMetricsQuestionnaireBinding
import com.almazov.diacompanion.questionnaire.models.BirthCount
import com.almazov.diacompanion.questionnaire.models.Drugs
import com.almazov.diacompanion.questionnaire.models.PregnancyNum
import com.almazov.diacompanion.questionnaire.models.Solarium
import com.almazov.diacompanion.questionnaire.models.VitaminDDosage
import com.almazov.diacompanion.questionnaire.models.YesNo

class QuestionnaireMetricsFragment : Fragment() {
    private var _binding: FragmentMetricsQuestionnaireBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMetricsQuestionnaireBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            spinnerPregnancyCount.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                PregnancyNum.values().map { it.text })
            spinnerBirthCount.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                BirthCount.values().map { it.text })
            spinnerOralContraceptive.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNo.values().map { it.text })

            spinnerProlactin.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNo.values().map { it.text })
            spinnerProlactinRaising.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNo.values().map { it.text })
            spinnerProlactinDrugs.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                Drugs.values().map { it.text })

            spinnerVitaminDBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                VitaminDDosage.values().map { it.text })
            spinnerVitaminD.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                VitaminDDosage.values().map { it.text })
            spinnerVacation.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNo.values().map { it.text })
            spinnerFirstTrim.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNo.values().map { it.text })
            spinnerSecondTrim.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNo.values().map { it.text })
            spinnerThirdTrim.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                YesNo.values().map { it.text })
            spinnerSolarium.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                Solarium.values().map { it.text })

            btnContinue.setOnClickListener { onContinueClick() }
        }
    }

    private fun onContinueClick() {
        with(binding) {
            val data = QuestionnaireEntity(
                pregnancyCount = spinnerPregnancyCount.selectedItem.toString(),
                birthCount = spinnerBirthCount.selectedItem.toString(),
                oralContraceptive = spinnerOralContraceptive.selectedItem.toString(),

                prolactin = spinnerProlactin.selectedItem.toString(),
                prolactinRaising = spinnerProlactinRaising.selectedItem.toString(),
                prolactinDrugs = spinnerProlactinDrugs.selectedItem.toString(),
                prolactinOtherDrugs = editTextProlactinOtherDrugs.text.toString(),

                vitaminDBefore = spinnerVitaminDBefore.selectedItem.toString(),
                vitaminDDrugsBefore = editTextVitaminDDrugsBefore.text.toString(),
                vitaminD = spinnerVitaminD.selectedItem.toString(),
                vitaminDDrugs = editTextVitaminDDrugs.text.toString(),
                vacation = spinnerVacation.selectedItem.toString(),
                firstTrim = spinnerFirstTrim.selectedItem.toString(),
                secondTrim = spinnerSecondTrim.selectedItem.toString(),
                thirdTrim = spinnerThirdTrim.selectedItem.toString(),
                solarium = spinnerSolarium.selectedItem.toString(),

                hba1c = editTextHba1c.text.toString().ifEmpty { "0" }.toFloat(),
                triglyceride = editTextTriglyceride.text.toString().ifEmpty { "0" }.toFloat(),
                cholesterol = editTextCholesterol.text.toString().ifEmpty { "0" }.toFloat(),
                glucose = editTextGlucose.text.toString().ifEmpty { "0" }.toFloat(),
                pregnancyAnalysesCount = editTextPregnancyAnalysis.text.toString().ifEmpty { "0" }.toInt()
            )

            findNavController().navigate(QuestionnaireMetricsFragmentDirections.actionQuestionnaireFragmentToQuestionnaireHealthFragment(data))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}