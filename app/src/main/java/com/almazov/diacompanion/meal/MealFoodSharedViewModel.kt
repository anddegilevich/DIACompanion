package com.almazov.diacompanion.meal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.almazov.diacompanion.data.FoodEntity

class MealFoodSharedViewModel: ViewModel() {
    private var _foodInMeal = MutableLiveData<List<FoodEntity>>()
    val foodInMeal: LiveData<List<FoodEntity>> = _foodInMeal

    private var _foodWeight = MutableLiveData<List<Double>>()
    val foodWeight: LiveData<List<Double>> = _foodWeight
}