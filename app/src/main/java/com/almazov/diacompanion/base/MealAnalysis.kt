package com.almazov.diacompanion.base

import android.content.Context
import android.content.res.Resources
import com.almazov.diacompanion.R
import com.almazov.diacompanion.meal.FoodInMealItem

fun checkGI(listOfFood: List<FoodInMealItem>): Boolean {
    for (food in listOfFood) {
        if (food.foodEntity.gi!! > 55) {
            return true
        }
    }
    return false
}

fun checkCarbs(mealType: String, listOfFood: List<FoodInMealItem>): Boolean {
    var sumCarbs = 0.0
    val  breakfastString = Resources.getSystem().getString(R.string.Breakfast)
    for (food in listOfFood) {
        sumCarbs += food.foodEntity.carbo!! * food.weight /100
    }
    if (sumCarbs > 30 && mealType == breakfastString) {
        return true
    } else if (sumCarbs > 60) {
        return true
    }
    return false
}

fun checkSLBefore(sugarLevel: Double): Boolean {
    if (sugarLevel > 6.7) {
        return true
    }
    return false
}

fun checkPV(listOfFood: List<FoodInMealItem>, sumPVToday: Double, sumPVYesterday: Double): Boolean {
    var sumPV = 0.0
    for (food in listOfFood) {
        sumPV += food.foodEntity.pv!! * food.weight / 100
    }
    if (sumPV < 8) {
        return true
    } else if (sumPV + sumPVToday < 20)
    {
        return true
    } else if (sumPV + sumPVYesterday < 28) return true
    return false
}

fun predictSL(BG0: Double, gl: Double, carbs: Double, protein: Double, mealType: String,
              kr: Double): Double {
    val breakfast = Resources.getSystem().getString(R.string.Breakfast)
    val lunch = Resources.getSystem().getString(R.string.Lunch)
    val dinner = Resources.getSystem().getString(R.string.Dinner)
    val snack = Resources.getSystem().getString(R.string.Snack)

    var t1 = 0.0
    var t2 = 0.0
    var t3 = 0.0
    var t4 = 0.0

    when (mealType) {
        breakfast -> t1 = 1.0
        lunch -> t2 = 1.0
        dinner -> t3 = 1.0
        snack -> t4 = 1.0
    }

    /*val sharedPreferences = context?.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
    val bmi = sharedPreferences!!.getBoolean("BMI")*/

    /*BG0 = уровень сахара до
    gl = гликемическая нагрузка
    carbo = углеводы текущий прием пищи
    prot = протеины за 6 часов до приема пищи без учета текущего

    1 0 0 0 - завтрак
    0 1 0 0 - обед
    0 0 1 0 - ужрин
    0 0 0 1 - прекус

    kr - текущий крахмал
    BMI - индекс массы тела*/



    return 0.0
}

fun getGL(listOfFood: List<FoodInMealItem>): Double {
    var gl = 0.0
    for (food in listOfFood) {
        gl += food.foodEntity.gi!! * food.foodEntity.carbo!!
    }
    gl /= 100
    return gl
}

fun getMessage(highGI: Boolean, manyCarbs: Boolean, highBGBefore: Boolean,
               lowPV: Boolean, bgPredict: Double): String {
    var txtInt: Int? = null
    if (highGI && bgPredict > 6.8) {
        txtInt = R.string.HighGI
    } else if (manyCarbs && bgPredict > 6.8) {
        txtInt = R.string.ManyCarbs
    } else if (highBGBefore && bgPredict > 6.8) {
        txtInt = R.string.HighBGBefore
    } else if (lowPV && bgPredict > 6.8) {
        txtInt = R.string.LowPV
    } else if (bgPredict > 6.8) {
        txtInt = R.string.BGPredict
    }
    return if (txtInt != null)
        Resources.getSystem().getString(txtInt)
    else ""
}