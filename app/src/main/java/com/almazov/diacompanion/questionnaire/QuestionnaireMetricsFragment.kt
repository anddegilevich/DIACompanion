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
import com.almazov.diacompanion.databinding.FragmentMetricsQuestionnaireBinding
import com.almazov.diacompanion.questionnaire.models.BirthCount
import com.almazov.diacompanion.questionnaire.models.Drugs
import com.almazov.diacompanion.questionnaire.models.PregnancyNum
import com.almazov.diacompanion.questionnaire.models.Solarium
import com.almazov.diacompanion.questionnaire.models.VitaminDDosage
import com.almazov.diacompanion.questionnaire.models.YesNo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class QuestionnaireMetricsFragment : Fragment() {
    private var _binding: FragmentMetricsQuestionnaireBinding? = null

    private val binding get() = _binding!!
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var data: QuestionnaireEntity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]
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
                    spinnerPregnancyCount.setSelectedByTitle(pregnancyCount)
                    spinnerBirthCount.setSelectedByTitle(birthCount)
                    spinnerOralContraceptive.setSelectedByTitle(oralContraceptive)

                    spinnerProlactin.setSelectedByTitle(prolactin)
                    spinnerProlactinRaising.setSelectedByTitle(prolactinRaising)
                    spinnerProlactinDrugs.setSelectedByTitle(prolactinDrugs)
                    editTextProlactinOtherDrugs.setText(prolactinOtherDrugs)

                    spinnerVitaminDBefore.setSelectedByTitle(vitaminDBefore)
                    editTextVitaminDDrugsBefore.setText(vitaminDDrugsBefore)
                    spinnerVitaminD.setSelectedByTitle(vitaminD)
                    editTextVitaminDDrugs.setText(vitaminDDrugs)
                    spinnerVacation.setSelectedByTitle(vacation)
                    spinnerFirstTrim.setSelectedByTitle(firstTrim)
                    spinnerSecondTrim.setSelectedByTitle(secondTrim)
                    spinnerThirdTrim.setSelectedByTitle(thirdTrim)
                    spinnerSolarium.setSelectedByTitle(solarium)

                    editTextHba1c.setText(hba1c.toString())
                    editTextTriglyceride.setText(triglyceride.toString())
                    editTextCholesterol.setText(cholesterol.toString())
                    editTextGlucose.setText(glucose.toString())
                    editTextPregnancyAnalysis.setText(pregnancyAnalysesCount.toString())
                }
            }
        }
    }

    private fun onContinueClick() {
        with(binding) {
            val entity = data ?: QuestionnaireEntity(id = 0)
            entity.apply {
                pregnancyCount = spinnerPregnancyCount.selectedItem.toString()
                birthCount = spinnerBirthCount.selectedItem.toString()
                oralContraceptive = spinnerOralContraceptive.selectedItem.toString()

                prolactin = spinnerProlactin.selectedItem.toString()
                prolactinRaising = spinnerProlactinRaising.selectedItem.toString()
                prolactinDrugs = spinnerProlactinDrugs.selectedItem.toString()
                prolactinOtherDrugs = editTextProlactinOtherDrugs.text.toString()

                vitaminDBefore = spinnerVitaminDBefore.selectedItem.toString()
                vitaminDDrugsBefore = editTextVitaminDDrugsBefore.text.toString()
                vitaminD = spinnerVitaminD.selectedItem.toString()
                vitaminDDrugs = editTextVitaminDDrugs.text.toString()
                vacation = spinnerVacation.selectedItem.toString()
                firstTrim = spinnerFirstTrim.selectedItem.toString()
                secondTrim = spinnerSecondTrim.selectedItem.toString()
                thirdTrim = spinnerThirdTrim.selectedItem.toString()
                solarium = spinnerSolarium.selectedItem.toString()

                hba1c = editTextHba1c.text.toString().ifEmpty { "0" }.toFloat()
                triglyceride = editTextTriglyceride.text.toString().ifEmpty { "0" }.toFloat()
                cholesterol = editTextCholesterol.text.toString().ifEmpty { "0" }.toFloat()
                glucose = editTextGlucose.text.toString().ifEmpty { "0" }.toFloat()
                pregnancyAnalysesCount = editTextPregnancyAnalysis.text.toString().ifEmpty { "0" }.toInt()
            }
            appDatabaseViewModel.saveQuestionnaire(entity)
            findNavController().navigate(QuestionnaireMetricsFragmentDirections.actionQuestionnaireFragmentToQuestionnaireHealthFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}