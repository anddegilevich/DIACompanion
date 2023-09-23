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
import com.almazov.diacompanion.databinding.FragmentFoodQuestionnaireBinding
import com.almazov.diacompanion.questionnaire.models.OneThreeRange
import com.almazov.diacompanion.questionnaire.models.OneTwoRange
import com.almazov.diacompanion.questionnaire.models.SixTwelveRange
import com.almazov.diacompanion.questionnaire.models.ThreeSixRange
import com.almazov.diacompanion.questionnaire.models.TwoFourRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class QuestionnaireFoodFragment : Fragment() {

    private var _binding: FragmentFoodQuestionnaireBinding? = null
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel
    private var data: QuestionnaireEntity? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]
        _binding = FragmentFoodQuestionnaireBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            spinnerFruitsBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SixTwelveRange.values().map { it.text })
            spinnerFruits.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SixTwelveRange.values().map { it.text })

            spinnerCupcakesBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                TwoFourRange.values().map { it.text })
            spinnerCupcakes.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                TwoFourRange.values().map { it.text })

            spinnerCakesBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                TwoFourRange.values().map { it.text })
            spinnerCakes.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                TwoFourRange.values().map { it.text })

            spinnerChocolateBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                TwoFourRange.values().map { it.text })
            spinnerChocolate.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                TwoFourRange.values().map { it.text })

            spinnerDefattedMilkBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                ThreeSixRange.values().map { it.text })
            spinnerDefattedMilk.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                ThreeSixRange.values().map { it.text })

            spinnerMilkBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                ThreeSixRange.values().map { it.text })
            spinnerMilk.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                ThreeSixRange.values().map { it.text })

            spinnerBeansBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneTwoRange.values().map { it.text })
            spinnerBeans.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneTwoRange.values().map { it.text })

            spinnerMeatBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                ThreeSixRange.values().map { it.text })
            spinnerMeat.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                ThreeSixRange.values().map { it.text })

            spinnerDryFruitsBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneThreeRange.values().map { it.text })
            spinnerDryFruits.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneThreeRange.values().map { it.text })

            spinnerFishBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                ThreeSixRange.values().map { it.text })
            spinnerFish.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                ThreeSixRange.values().map { it.text })

            spinnerWholemealBreadBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneThreeRange.values().map { it.text })
            spinnerWholemealBread.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneThreeRange.values().map { it.text })

            spinnerBreadBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SixTwelveRange.values().map { it.text })
            spinnerBread.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SixTwelveRange.values().map { it.text })

            spinnerSauceBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                TwoFourRange.values().map { it.text })
            spinnerSauce.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                TwoFourRange.values().map { it.text })

            spinnerVegetablesBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SixTwelveRange.values().map { it.text })
            spinnerVegetables.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                SixTwelveRange.values().map { it.text })

            spinnerAlcoholBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneThreeRange.values().map { it.text })
            spinnerAlcohol.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneThreeRange.values().map { it.text })

            spinnerSweetDrinksBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                TwoFourRange.values().map { it.text })
            spinnerSweetDrinks.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                TwoFourRange.values().map { it.text })

            spinnerCoffeeBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneThreeRange.values().map { it.text })
            spinnerCoffee.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneThreeRange.values().map { it.text })

            spinnerSausagesBefore.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneThreeRange.values().map { it.text })
            spinnerSausages.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                OneThreeRange.values().map { it.text })

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
                    spinnerFruitsBefore.setSelectedByTitle(fruitsBefore)
                    spinnerFruits.setSelectedByTitle(fruits)

                    spinnerCupcakesBefore.setSelectedByTitle(cupcakesBefore)
                    spinnerCupcakes.setSelectedByTitle(cupcakes)

                    spinnerCakesBefore.setSelectedByTitle(cakesBefore)
                    spinnerCakes.setSelectedByTitle(cakes)

                    spinnerChocolateBefore.setSelectedByTitle(chocolateBefore)
                    spinnerChocolate.setSelectedByTitle(chocolate)

                    spinnerDefattedMilkBefore.setSelectedByTitle(defattedMilkBefore)
                    spinnerDefattedMilk.setSelectedByTitle(defattedMilk)

                    spinnerMilkBefore.setSelectedByTitle(milkBefore)
                    spinnerMilk.setSelectedByTitle(milk)

                    spinnerBeansBefore.setSelectedByTitle(beansBefore)
                    spinnerBeans.setSelectedByTitle(beans)

                    spinnerMeatBefore.setSelectedByTitle(meatBefore)
                    spinnerMeat.setSelectedByTitle(meat)

                    spinnerDryFruitsBefore.setSelectedByTitle(dryFruitsBefore)
                    spinnerDryFruits.setSelectedByTitle(dryFruits)

                    spinnerFishBefore.setSelectedByTitle(fishBefore)
                    spinnerFish.setSelectedByTitle(fish)

                    spinnerWholemealBreadBefore.setSelectedByTitle(wholemealBreadBefore)
                    spinnerWholemealBread.setSelectedByTitle(wholemealBread)

                    spinnerBreadBefore.setSelectedByTitle(breadBefore)
                    spinnerBread.setSelectedByTitle(bread)

                    spinnerSauceBefore.setSelectedByTitle(sauceBefore)
                    spinnerSauce.setSelectedByTitle(sauce)

                    spinnerVegetablesBefore.setSelectedByTitle(vegetablesBefore)
                    spinnerVegetables.setSelectedByTitle(vegetables)

                    spinnerAlcoholBefore.setSelectedByTitle(alcoholBefore)
                    spinnerAlcohol.setSelectedByTitle(alcohol)

                    spinnerSweetDrinksBefore.setSelectedByTitle(sweetDrinksBefore)
                    spinnerSweetDrinks.setSelectedByTitle(sweetDrinks)

                    spinnerCoffeeBefore.setSelectedByTitle(coffeeBefore)
                    spinnerCoffee.setSelectedByTitle(coffee)

                    spinnerSausagesBefore.setSelectedByTitle(sausagesBefore)
                    spinnerSausages.setSelectedByTitle(sausages)
                }
            }
        }
    }

    private fun onContinueClick() {
        with(binding) {
            data?.apply {
                fruitsBefore = spinnerFruitsBefore.selectedItem.toString()
                fruits = spinnerFruits.selectedItem.toString()
                cupcakesBefore = spinnerCupcakesBefore.selectedItem.toString()
                cupcakes = spinnerCupcakes.selectedItem.toString()
                cakesBefore = spinnerCakesBefore.selectedItem.toString()
                cakes = spinnerCakes.selectedItem.toString()
                chocolateBefore = spinnerChocolateBefore.selectedItem.toString()
                chocolate = spinnerChocolate.selectedItem.toString()
                defattedMilkBefore = spinnerDefattedMilkBefore.selectedItem.toString()
                defattedMilk = spinnerDefattedMilk.selectedItem.toString()
                milkBefore = spinnerMilkBefore.selectedItem.toString()
                milk = spinnerMilk.selectedItem.toString()
                beansBefore = spinnerBeansBefore.selectedItem.toString()
                beans = spinnerBeans.selectedItem.toString()
                meatBefore = spinnerMeatBefore.selectedItem.toString()
                meat = spinnerMeat.selectedItem.toString()
                dryFruitsBefore = spinnerDryFruitsBefore.selectedItem.toString()
                dryFruits = spinnerDryFruits.selectedItem.toString()
                fishBefore = spinnerFishBefore.selectedItem.toString()
                fish = spinnerFish.selectedItem.toString()
                wholemealBreadBefore = spinnerWholemealBreadBefore.selectedItem.toString()
                wholemealBread = spinnerWholemealBread.selectedItem.toString()
                breadBefore = spinnerBreadBefore.selectedItem.toString()
                bread = spinnerBread.selectedItem.toString()
                sauceBefore = spinnerSauceBefore.selectedItem.toString()
                sauce = spinnerSauce.selectedItem.toString()
                vegetablesBefore = spinnerVegetablesBefore.selectedItem.toString()
                vegetables = spinnerVegetables.selectedItem.toString()
                alcoholBefore = spinnerAlcoholBefore.selectedItem.toString()
                alcohol = spinnerAlcohol.selectedItem.toString()
                sweetDrinksBefore = spinnerSweetDrinksBefore.selectedItem.toString()
                sweetDrinks = spinnerSweetDrinks.selectedItem.toString()
                coffeeBefore = spinnerCoffeeBefore.selectedItem.toString()
                coffee = spinnerCoffee.selectedItem.toString()
                sausagesBefore = spinnerSausagesBefore.selectedItem.toString()
                sausages = spinnerSausages.selectedItem.toString()
            }
            data?.let {
                appDatabaseViewModel.saveQuestionnaire(it)
            }
            findNavController().navigate(
                QuestionnaireFoodFragmentDirections.actionQuestionnaireFoodFragmentToQuestionnaireSportsFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}