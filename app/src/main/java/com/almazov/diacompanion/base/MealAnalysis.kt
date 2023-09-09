package com.almazov.diacompanion.base

import android.content.Context
import android.content.res.Resources
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.MealWithFood
import com.almazov.diacompanion.meal.FoodInMealItem
import kotlin.math.floor
import ai.catboost.CatBoostModel


fun checkGI(listOfFood: MutableList<FoodInMealItem>): Boolean {
    for (food in listOfFood) {
        if (food.foodEntity.gi!! > 55) {
            return true
        }
    }
    return false
}

fun checkCarbs(mealType: String, listOfFood: MutableList<FoodInMealItem>): Boolean {
    var sumCarbs = 0.0
    val breakfastString = "Завтрак"
    for (food in listOfFood) {
        sumCarbs += food.foodEntity.carbo!! * food.weight / 100
    }
    if (sumCarbs > 15 && mealType == breakfastString) {
        return true
    } else if (sumCarbs > 30) {
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

fun checkPV(
    listOfFood: List<MealWithFood>,
    listOfFoodToday: List<MealWithFood>,
    listOfFoodYesterday: List<MealWithFood>
): Boolean {
    var sumPV = 0.0
    var sumPVToday = 0.0
    var sumPVYesterday = 0.0
    for (food in listOfFood) {
        sumPV += food.food.pv!! * food.weight!! / 100
    }
    for (food in listOfFoodToday) {
        sumPVToday += food.food.pv!! * food.weight!! / 100
    }
    for (food in listOfFoodYesterday) {
        sumPVYesterday += food.food.pv!! * food.weight!! / 100
    }

    if (sumPV < 8) {
        return true
    } else if (sumPV + sumPVToday < 20) {
        return true
    } else if (sumPV + sumPVToday + sumPVYesterday < 28) return true
    return false
}

suspend fun predictSL(
    context: Context,
    bg0: Float,
    iterablePredictors: IterablePredictors,
    sixHoursPredictors: SixHoursPredictors,
    pvTwelveHours: Float,
    mealType: String,
    bmi: Float,
    hbA1C: Float,
    tg: Float,
    hol: Float,
    weight: Float,
    age: Float,
    glucoseNt: Float,
    analysisTime: Float

): Double {
    val breakfast = "Завтрак"
    val lunch = "Обед"
    val dinner = "Ужин"
    val snack = "Перекус"

    val mealTypeFloat = when (mealType) {
        breakfast -> 1f
        lunch -> 2f
        dinner -> 3f
        snack -> 4f
        else -> 1f
    }

    val assetManager = context.assets
    val inputStream = assetManager.open("model.cbm")
    val catboostModel = CatBoostModel.loadModel(inputStream)

    val numericFeatures = floatArrayOf(
        mealTypeFloat,
        iterablePredictors.gi,
        iterablePredictors.gl,
        iterablePredictors.carbs,
        iterablePredictors.mds,
        iterablePredictors.kr,
        iterablePredictors.ca,
        iterablePredictors.fe,
        sixHoursPredictors.carbs,
        sixHoursPredictors.protein,
        sixHoursPredictors.fat,
        pvTwelveHours,
        bg0,
        bmi,
        hbA1C,
        tg,
        hol,
        weight,
        age,
        glucoseNt,
        analysisTime
    )

    val catFeatures: Array<String>? = null

    val prediction = catboostModel.predict(numericFeatures, catFeatures, null, null)
    prediction.get(0, 1)

    return prediction.get(0, 1)
}

fun getSixHoursPredictors(listOfFood: List<MealWithFood>): SixHoursPredictors {
    var carbs = 0.0
    var protein = 0.0
    var fat = 0.0
    for (food in listOfFood) {
        val weight = food.weight!! / 100
        carbs += food.food.carbo!! * weight
        protein += food.food.prot!! * weight
        fat += food.food.fat!! * weight
    }
    return SixHoursPredictors(
        carbs = carbs.toFloat(),
        protein = protein.toFloat(),
        fat = fat.toFloat()
    )
}
fun getPv(listOfFood: List<MealWithFood>): Float {
    var pv = 0.0
    for (food in listOfFood) {
        val weight = food.weight!! / 100
        pv += food.food.pv!! * weight
    }
    return pv.toFloat()
}

fun getGLCarbsKr(listOfFood: List<FoodInMealItem>): Pair<IterablePredictors, Boolean> {
    val gl = mutableListOf<Double>()
    var carbs = 0.0
    var krs = 0.0
    var mdsSum = 0.0
    var caSum = 0.0
    var feSum = 0.0
    for (food in listOfFood) {
        with(food.foodEntity) {
            val weight = food.weight / 100
            val gi = gi ?: 0.0
            val carb = carbo ?: 0.0
            val kr = kr ?: 0.0
            val mds = mds ?: 0.0
            val ca = ca ?: 0.0
            val fe = fe ?: 0.0
            gl.add(gi * carb * weight)
            carbs += carb * weight
            krs += kr * weight
            mdsSum += mds * weight
            caSum += ca * weight
            feSum += fe * weight
        }
    }
    val giSum = gl.sum() / carbs
    val glSum = gl.sum() / 100
    gl.sortDescending()
    var glMax = 0.0
    for (i in 0 until floor(gl.size.toDouble() / 2).toInt()) {
        gl[i] = gl[i] / glSum
        glMax += gl[i]
    }
    val glDistribution = glMax > 60
    val iterablePredictors = IterablePredictors(
        gi = giSum.toFloat(),
        gl = glSum.toFloat(),
        carbs = carbs.toFloat(),
        mds = mdsSum.toFloat(),
        kr = krs.toFloat(),
        ca = caSum.toFloat(),
        fe = feSum.toFloat()
    )
    return iterablePredictors to glDistribution
}

fun getMealInfo(listOfFood: List<FoodInMealItem>): List<Double> {
    var proteins = 0.0
    var fats = 0.0
    var carbs = 0.0
    var weight = 0.0
    var kkals = 0.0
    var gl = 0.0
    for (food in listOfFood) {
        val protein = food.foodEntity.prot ?: 0.0
        val fat = food.foodEntity.fat ?: 0.0
        val carb = food.foodEntity.carbo ?: 0.0
        val gi = food.foodEntity.gi ?: 0.0
        val kkal = food.foodEntity.ec ?: 0.0
        proteins += protein * food.weight
        fats += fat * food.weight
        carbs += carb * food.weight
        kkals += kkal * food.weight
        gl += gi * carb * food.weight
        weight += food.weight
    }
    val gis = gl / carbs
    return listOf(
        proteins / 100, fats / 100, carbs / 100, kkals / 100, gis, gl / 10000,
        weight
    )
}

fun getMessage(
    highGI: Boolean, manyCarbs: Boolean, highBGBefore: Boolean,
    glDistribution: Boolean, highBGPredict: Boolean, resources: Resources
): List<String> {
    val recommendations = mutableListOf<String>()
    val txtInt = if (highBGPredict) {

        if (highBGBefore) {
            recommendations.add(resources.getString(R.string.HighBGBefore))
        }

        if (manyCarbs) {
            if (glDistribution) {
                R.string.GLDestribution
            } else if (highGI) {
                R.string.HighGI
            } else {
                R.string.ManyCarbs
            }
        } else {
            R.string.BGPredict
        }
    } else R.string.NoRecommendation
    recommendations.add(resources.getString(txtInt))
    return recommendations
}

fun checkHyperglycemia(chance: Double): Boolean {
    return chance > 0.52
}