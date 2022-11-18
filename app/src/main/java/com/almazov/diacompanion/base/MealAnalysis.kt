package com.almazov.diacompanion.base

import android.content.Context
import android.content.res.Resources
import biz.k11i.xgboost.Predictor
import biz.k11i.xgboost.util.FVec
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.MealWithFood
import com.almazov.diacompanion.meal.FoodInMealItem
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


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

fun predictSL(
    context: Context,
    BG0: Double?,
    glCarbsKr: List<Double?>,
    protein: Double?,
    mealType: String,
    bmi: Double?
): Double {
   /* val breakfast = Resources.getSystem().getString(R.string.Breakfast)
    val lunch = Resources.getSystem().getString(R.string.Lunch)
    val dinner = Resources.getSystem().getString(R.string.Dinner)
    val snack = Resources.getSystem().getString(R.string.Snack)*/

    val breakfast = "Завтрак"
    val lunch = "Обед"
    val dinner = "Ужин"
    val snack = "Перекус"

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


    val modelPath: InputStream = context.assets.open("predict_model.model")
    val predictor = Predictor(modelPath)

    val denseArray = doubleArrayOf(
        BG0!!, glCarbsKr[0]!!, glCarbsKr[1]!!,
        protein!!, t1, t2, t3, t4, glCarbsKr[2]!!, bmi!!
    )
    val fVecDense: FVec = FVec.Transformer.fromArray(denseArray, false)

    return predictor.predictSingle(fVecDense)
}

fun getProtein(listOfFood: List<MealWithFood>): Double {
    var protein = 0.0
    for (food in listOfFood) {
        protein += food.food.prot!! * food.weight!! / 100
    }
    return protein
}

fun getGLCarbsKr(listOfFood: List<FoodInMealItem>): List<Double> {
    var gl = 0.0
    var carbs = 0.0
    var kr = 0.0
    for (food in listOfFood) {
        gl += food.foodEntity.gi!! * food.foodEntity.carbo!! * food.weight / 100
        carbs += food.foodEntity.carbo * food.weight / 100
        kr += food.foodEntity.kr!! * food.weight / 100
    }
    gl /= 100
    return listOf(gl, carbs, kr)
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

@Throws(IOException::class)
fun getFileFromAssets(context: Context, fileName: String): File = File(context.cacheDir, fileName)
    .also {
        if (!it.exists()) {
            it.outputStream().use { cache ->
                context.assets.open(fileName).use { inputStream ->
                    inputStream.copyTo(cache)
                }
            }
        }
    }