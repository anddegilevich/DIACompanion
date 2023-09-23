package com.almazov.diacompanion.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "questionnaire_table")
data class QuestionnaireEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    //1 page
    //Pregnancy
    var pregnancyCount: String? = null,
    var birthCount: String? = null,
    var oralContraceptive: String? = null,

    //Prolactin
    var prolactin: String? = null,
    var prolactinRaising: String? = null,
    var prolactinDrugs: String? = null,
    var prolactinOtherDrugs: String? = null,

    //VitaminD
    var vitaminDBefore: String? = null,
    var vitaminDDrugsBefore: String? = null,
    var vitaminD: String? = null,
    var vitaminDDrugs: String? = null,
    var vacation: String? = null,
    var firstTrim: String? = null,
    var secondTrim: String? = null,
    var thirdTrim: String? = null,
    var solarium: String? = null,

    //OtherAnalyses
    var hba1c: Float? = null,
    var triglyceride: Float? = null,
    var cholesterol: Float? = null,
    var glucose: Float? = null,
    var pregnancyAnalysesCount: Int? = null,

    //2 Health
    //Diabetes
    var diabetesRelative: String? = null,
    var glucoseTolerance: String? = null,

    //Hypertension
    var hypertensionBefore: String? = null,
    var hypertension: String? = null,

    //Smoking
    var smoking6MonthBefore: String? = null,
    var smokingBefore: String? = null,
    var smoking: String? = null,

    //3 Food
    //Fruits
    var fruitsBefore: String? = null,
    var fruits: String? = null,

    //Cupcakes
    var cupcakesBefore: String? = null,
    var cupcakes: String? = null,

    //Cakes
    var cakesBefore: String? = null,
    var cakes: String? = null,

    //Chocolate
    var chocolateBefore: String? = null,
    var chocolate: String? = null,

    //Defatted milk
    var defattedMilkBefore: String? = null,
    var defattedMilk: String? = null,

    //Milk
    var milkBefore: String? = null,
    var milk: String? = null,

    //Beans
    var beansBefore: String? = null,
    var beans: String? = null,

    //Meat
    var meatBefore: String? = null,
    var meat: String? = null,

    //Dry fruits
    var dryFruitsBefore: String? = null,
    var dryFruits: String? = null,

    //Fish
    var fishBefore: String? = null,
    var fish: String? = null,

    //Wholemeal bread
    var wholemealBreadBefore: String? = null,
    var wholemealBread: String? = null,

    //Bread
    var breadBefore: String? = null,
    var bread: String? = null,

    //Sauce
    var sauceBefore: String? = null,
    var sauce: String? = null,

    //Vegetables
    var vegetablesBefore: String? = null,
    var vegetables: String? = null,

    //Alcohol
    var alcoholBefore: String? = null,
    var alcohol: String? = null,

    //SweetDrinks
    var sweetDrinksBefore: String? = null,
    var sweetDrinks: String? = null,

    //CoffeeDrinks
    var coffeeBefore: String? = null,
    var coffee: String? = null,

    //SausagesDrinks
    var sausagesBefore: String? = null,
    var sausages: String? = null,

    //3 Sport
    //Walking
    var walkingBefore: String? = null,
    var walking: String? = null,

    //Stepping
    var steppingBefore: String? = null,
    var stepping: String? = null,

    //Sport
    var sportBefore: String? = null,
    var sport: String? = null,
): Parcelable