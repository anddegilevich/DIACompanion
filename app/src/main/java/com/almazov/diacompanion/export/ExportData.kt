package com.almazov.diacompanion.export

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.almazov.diacompanion.R
import com.almazov.diacompanion.base.setTwoDigits
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_export_data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.RegionUtil
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExportData : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appType: String
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel

    private lateinit var globalInfoString: String

    private lateinit var styleRed: XSSFCellStyle
    private lateinit var styleYellow: XSSFCellStyle
    private lateinit var styleBlue: XSSFCellStyle
    private lateinit var styleSeaGreen: XSSFCellStyle
    private lateinit var styleNormal: XSSFCellStyle


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_export_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        appType = sharedPreferences.getString("APP_TYPE","GDM RCT")!!

        btn_export_to_slxs.setOnClickListener{
            lf_export.displayedChild = 1
            createXmlFile()
        }

        btn_export_to_doctor.setOnClickListener{
//            findNavController().popBackStack()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun createXmlFile() {

        val xlWb = XSSFWorkbook()
        styleNormal = xlWb.createCellStyle().apply {
            wrapText = true
            fillForegroundColor = IndexedColors.WHITE.getIndex()
            fillPattern = FillPatternType.SOLID_FOREGROUND
            borderTop = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
        }

        styleRed = styleNormal.copy()
        styleRed.apply {
            fillForegroundColor = IndexedColors.RED.getIndex()
        }

        styleYellow = styleNormal.copy()
        styleYellow.apply {
            fillForegroundColor = IndexedColors.YELLOW.getIndex()
        }

        styleBlue = styleNormal.copy()
        styleBlue.apply {
            fillForegroundColor = IndexedColors.BLUE.getIndex()
        }

        styleSeaGreen = styleNormal.copy()
        styleSeaGreen.apply {
            fillForegroundColor = IndexedColors.SEA_GREEN.getIndex()
        }

        val name = sharedPreferences.getString("NAME","Имя")
        val secondName = sharedPreferences.getString("SECOND_NAME","Фамилия")
        val patronymic = sharedPreferences.getString("PATRONYMIC","Отчество")
        val attendingDoctor = sharedPreferences.getString("ATTENDING_DOCTOR","Лечащий врач")
        val birthDate = sharedPreferences.getString("BIRTH_DATE","0")
        globalInfoString = "Пациент: $secondName $name $patronymic;   " +
                "Дата рождения: $birthDate;   Лечащий врач: $attendingDoctor;   " +
                "Программа: DiaCompanion Android $appType"

        val xlWsSugarLevelInsulin = xlWb.createSheet("Уровень сахара и инсулин")
        val xlWsMeal = xlWb.createSheet("Приемы пищи")
        val xlWsWorkoutSleep = xlWb.createSheet("Физическая нагрузка и сон")
        val xlWsWeight = xlWb.createSheet("Масса тела")
        val xlWsKetone = xlWb.createSheet("Кетоны в моче")
        val xlWsFullDay = xlWb.createSheet("Полные дни")
        GlobalScope.launch(Dispatchers.Main) {
            val sugarLevelInsulinSheetCompleted = GlobalScope.async(Dispatchers.Default) {
                getSugarLevelAndInsulinTable(xlWsSugarLevelInsulin)
            }
            val mealSheetCompleted = GlobalScope.async(Dispatchers.Default) {
                getMealTable(xlWsMeal)
            }
            val workoutSleepSheetCompleted = GlobalScope.async(Dispatchers.Default) {
                getWorkoutAndSleepTable(xlWsWorkoutSleep)
            }
            val weightSheetCompleted = GlobalScope.async(Dispatchers.Default) {
                getWeightTable(xlWsWeight)
            }
            val ketoneSheetCompleted = GlobalScope.async(Dispatchers.Default) {
                getKetoneTable(xlWsKetone)
            }
            val fullDaysSheetCompleted = GlobalScope.async(Dispatchers.Default) {
                getFullDaysTable(xlWsFullDay)
            }
            if (sugarLevelInsulinSheetCompleted.await() and workoutSleepSheetCompleted.await() and
                weightSheetCompleted.await() and ketoneSheetCompleted.await() and
                fullDaysSheetCompleted.await() and mealSheetCompleted.await()) {

                val path = requireContext().getDatabasePath("exportTest.xlsx").path
                val table = FileOutputStream(path)

                xlWb.write(table)
                xlWb.close()

                val uriPath = Uri.parse(path)
                val excelIntent = Intent()
                excelIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                excelIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                excelIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                excelIntent.action = Intent.ACTION_VIEW
                excelIntent.setDataAndType(uriPath, "application/vnd.ms-excel")
                excelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                try {
                    startActivity(excelIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        requireContext(),
                        "No Application available to view Excel",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                lf_export.displayedChild = 2
                findNavController().popBackStack()
            }
        }

    }

    private suspend fun getMealTable(sheet: XSSFSheet): Boolean {
        sheet.defaultColumnWidth = 16

        val mealRecords = GlobalScope.async(Dispatchers.Default) {
            appDatabaseViewModel.readAllMealRecords()
        }

        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 13))
        sheet.createRow(0).createCell(0).setCellValue(globalInfoString)
        sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 67))
        sheet.createRow(1).createCell(0).apply {
            setCellValue("Приемы пищи")
            cellStyle = styleYellow
        }

        val columnNames = listOf("Дата", "Время", "Прием пищи", "Продукт", "Масса (г)",
            "Углеводы (г)", "Белки (г)", "Жиры (г)", "Энерг. ценность (Ккал)", "ГИ", "ГН","",
            "Вода (г)", "НЖК (г)", "Холестерин (мг)", "Пищевые волокна (г)",
            "Зола (г)", "Натрий (мг)", "Калий (мг)", "Кальций (мг)", "Магний (мг)", "Фосфор (мг)",
            "Железо (мг)", "Ретинол (мкг)", "Тиамин (мг)", "Рибофлавин (мг)", "Ниацин (мг)",
            "Аскорбиновая кисл. (мг)", "Ретиновый эквивалент (мкг)", "Бета-каротин (мкг)",
            "МДС (г)", "Крахмал (г)", "Токоферолэквивалент (мг)",
            "Органические кислоты (г)", "Ниациновый эквивалент (мг)", "Цинк (мг)", "Медь (мг)",
            "Марганец (мг)", "Селен (мкг)", "Пантотеновая кислота (мг)", "Витамин B6 (мг)",
            "Фолаты общ. (мкг)", "Фолиевая кислота (мкг)", "Фолаты ДФЭ (мкг)", "Холин общ. (мкг)",
            "Витамин B12 (мг)", "Витамин A (ЭАР)", "Альфа-каротин (мкг)",
            "Криптоксантин бета (мкг)", "Ликопин (мкг)", "Лютеин + Гексаксантин (мкг)",
            "Витамин E (мг)", "Витамин D (мкг)", "Витамин D (межд.ед.)", "Витамин K (мкг)",
            "Мононенасыщенные жирные кислоты (г)", "Полиненасыщенные жирные кислоты (г)", "",
            "Вес перв. ед. изм.", "Описание перв. ед. изм.", "Вес второй ед. изм",
            "Опис. второй ед. изм.", "Процент потерь, %", "", "УСК до еды", "Прогноз УСК",
            "Время добавления записи"
        )
        sheet.createRow(2).apply {

            if ((appType == "GDMRCT") or (appType == "GDM")) {
                createCell(0).apply {
                    setCellValue("Неделя беременности")
                    cellStyle = styleYellow
                }
            }

            var i = 1
            for (columnName in columnNames) {
                createCell(i).apply {
                    setCellValue(columnName)
                    cellStyle = styleYellow
                }
                i += 1
            }
        }

        val mealList = mealRecords.await().toList()
        if (mealList.isNullOrEmpty()) return true
        val dates = getDates(mealList[0].recordEntity.date!!, mealList.last().recordEntity.date!!)

        var i = 3
        var j = 0

        val carbo = mutableListOf<Pair<Double,String>>()
        val prot = mutableListOf<Pair<Double,String>>()
        val fat = mutableListOf<Pair<Double,String>>()
        val ec = mutableListOf<Pair<Double,String>>()
        val gi = mutableListOf<Pair<Double,String>>()
        val water = mutableListOf<Pair<Double,String>>()
        val nzhk = mutableListOf<Pair<Double,String>>()
        val hol = mutableListOf<Pair<Double,String>>()
        val pv = mutableListOf<Pair<Double,String>>()
        val zola = mutableListOf<Pair<Double,String>>()
        val na = mutableListOf<Pair<Double,String>>()
        val k = mutableListOf<Pair<Double,String>>()
        val ca = mutableListOf<Pair<Double,String>>()
        val mg = mutableListOf<Pair<Double,String>>()
        val p = mutableListOf<Pair<Double,String>>()
        val fe = mutableListOf<Pair<Double,String>>()
        val a = mutableListOf<Pair<Double,String>>()
        val b1 = mutableListOf<Pair<Double,String>>()
        val b2 = mutableListOf<Pair<Double,String>>()
        val rr = mutableListOf<Pair<Double,String>>()
        val c = mutableListOf<Pair<Double,String>>()
        val re = mutableListOf<Pair<Double,String>>()

        val carboMeal = mutableListOf<Double>()
        val protMeal = mutableListOf<Double>()
        val fatMeal = mutableListOf<Double>()
        val ecMeal = mutableListOf<Double>()
        val giMeal = mutableListOf<Double>()
        val waterMeal = mutableListOf<Double>()
        val nzhkMeal = mutableListOf<Double>()
        val holMeal = mutableListOf<Double>()
        val pvMeal = mutableListOf<Double>()
        val zolaMeal = mutableListOf<Double>()
        val naMeal = mutableListOf<Double>()
        val kMeal = mutableListOf<Double>()
        val caMeal = mutableListOf<Double>()
        val mgMeal = mutableListOf<Double>()
        val pMeal = mutableListOf<Double>()
        val feMeal = mutableListOf<Double>()
        val aMeal = mutableListOf<Double>()
        val b1Meal = mutableListOf<Double>()
        val b2Meal = mutableListOf<Double>()
        val rrMeal = mutableListOf<Double>()
        val cMeal = mutableListOf<Double>()
        val reMeal = mutableListOf<Double>()

        for (date in dates) {
            if (mealList[j].recordEntity.date == date.date) {
                try {
                    while  (mealList[j].recordEntity.date == date.date) {
                        sheet.createRow(i).apply {
                            createCell(1).apply {
                                setCellValue(date.date)
                                cellStyle = styleNormal
                            }
                            createCell(2).apply {
                                setCellValue(mealList[j].recordEntity.time)
                                cellStyle = styleNormal
                            }
                            createCell(3).apply {
                                setCellValue(mealList[j].mealWithFoods.mealEntity.type)
                                cellStyle = styleNormal
                            }
                            var cellName = ""
                            var cellWeight = ""
                            var cellCarbs = ""
                            var cellFats = ""
                            var cellProtein = ""
                            var cellKCal = ""
                            var cellGI = ""
                            var cellGL = ""

                            var cellWater = ""
                            var cellNzhk = ""
                            var cellHol = ""
                            var cellPV = ""
                            var cellZola = ""
                            var cellNa = ""
                            var cellK = ""
                            var cellCa = ""
                            var cellMg = ""
                            var cellP = ""
                            var cellFe = ""
                            var cellA = ""
                            var cellB1 = ""
                            var cellB2 = ""
                            var cellRr = ""
                            var cellC = ""
                            var cellRe = ""
                            var cellKar = ""
                            var cellMds = ""
                            var cellKr = ""
                            var cellTe = ""
                            var cellOk = ""
                            var cellNe = ""
                            var cellZn = ""
                            var cellCu = ""
                            var cellMn = ""
                            var cellSe = ""
                            var cellB5 = ""
                            var cellB6 = ""
                            var cellFol = ""
                            var cellB9 = ""
                            var cellDfe = ""
                            var cellHolin = ""
                            var cellB12 = ""
                            var cellEar = ""
                            var cellAKar = ""
                            var cellBKript = ""
                            var cellLikopin = ""
                            var cellLutZ = ""
                            var cellVitE = ""
                            var cellVitD = ""
                            var cellDMezd = ""
                            var cellVitK = ""
                            var cellMzhk = ""
                            var cellPzhk = ""

                            var cellW1Ed = ""
                            var cellOp1Ed = ""
                            var cellW2Ed = ""
                            var cellOp2Ed = ""
                            var cellProcProt = ""

                            for (food in mealList[j].mealWithFoods.foods) {
                                val weight = food.foodInMealEntity.weight!! / 100
                                cellName += "\n" + food.food.name.toString() + "\n"
                                cellWeight += setTwoDigits(food.foodInMealEntity.weight).toString() + "\n"
                                if (food.food.carbo != null) {
                                    cellCarbs += setTwoDigits(weight * food.food.carbo).toString() + "\n"
                                    carboMeal.add(weight * food.food.carbo)
                                }
                                if (food.food.fat != null) {
                                    cellFats += setTwoDigits(weight * food.food.fat).toString() + "\n"
                                    fatMeal.add(weight * food.food.fat)
                                }
                                if (food.food.prot != null) {
                                    cellProtein += setTwoDigits(weight * food.food.prot).toString() + "\n"
                                    protMeal.add(weight * food.food.prot)
                                }
                                if (food.food.ec != null) {
                                    cellKCal += setTwoDigits(weight * food.food.ec).toString() + "\n"
                                    ecMeal.add(weight * food.food.ec)
                                }
                                if (food.food.gi != null) {
                                    cellGI += setTwoDigits(weight * food.food.gi).toString() + "\n"
                                    giMeal.add(weight * food.food.gi)
                                }
                                cellGL += setTwoDigits(weight*food.food.carbo!!*food.food.gi!!).toString() + "\n"

                                if (food.food.water != null) {
                                    cellWater += setTwoDigits(weight * food.food.water).toString() + "\n"
                                    waterMeal.add(weight * food.food.water)
                                }
                                if (food.food.nzhk != null) {
                                    cellNzhk += setTwoDigits(weight * food.food.nzhk).toString() + "\n"
                                    nzhkMeal.add(weight * food.food.nzhk)
                                }
                                if (food.food.hol != null) {
                                    cellHol += setTwoDigits(weight * food.food.hol).toString() + "\n"
                                    holMeal.add(weight * food.food.hol)
                                }
                                if (food.food.pv != null) {
                                    cellPV += setTwoDigits(weight * food.food.pv).toString() + "\n"
                                    pvMeal.add(weight * food.food.pv)
                                }
                                if (food.food.zola != null) {
                                    cellZola += setTwoDigits(weight * food.food.zola).toString() + "\n"
                                    zolaMeal.add(weight * food.food.zola)
                                }
                                if (food.food.na != null) {
                                    cellNa += setTwoDigits(weight * food.food.na).toString() + "\n"
                                    naMeal.add(weight * food.food.na)
                                }
                                if (food.food.k != null) {
                                    cellK += setTwoDigits(weight * food.food.k).toString() + "\n"
                                    kMeal.add(weight * food.food.k)
                                }
                                if (food.food.ca != null) {
                                    cellCa += setTwoDigits(weight * food.food.ca).toString() + "\n"
                                    caMeal.add(weight * food.food.ca)
                                }
                                if (food.food.mg != null) {
                                    cellMg += setTwoDigits(weight * food.food.mg).toString() + "\n"
                                    mgMeal.add(weight * food.food.mg)
                                }
                                if (food.food.p != null) {
                                    cellP += setTwoDigits(weight * food.food.p).toString() + "\n"
                                    pMeal.add(weight * food.food.p)
                                }
                                if (food.food.fe != null) {
                                    cellFe += setTwoDigits(weight * food.food.fe).toString() + "\n"
                                    feMeal.add(weight * food.food.fe)
                                }
                                if (food.food.a != null) {
                                    cellA += setTwoDigits(weight * food.food.a).toString() + "\n"
                                    aMeal.add(weight * food.food.a)
                                }
                                if (food.food.b1 != null) {
                                    cellB1 += setTwoDigits(weight * food.food.b1).toString() + "\n"
                                    b1Meal.add(weight * food.food.b1)
                                }
                                if (food.food.b2 != null) {
                                    cellB2 += setTwoDigits(weight * food.food.b2).toString() + "\n"
                                    b2Meal.add(weight * food.food.b2)
                                }
                                if (food.food.rr != null) {
                                    cellRr += setTwoDigits(weight * food.food.rr).toString() + "\n"
                                    rrMeal.add(weight * food.food.rr)
                                }
                                if (food.food.c != null) {
                                    cellC += setTwoDigits(weight * food.food.c).toString() + "\n"
                                    cMeal.add(weight * food.food.c)
                                }
                                if (food.food.re != null) {
                                    cellRe += setTwoDigits(weight * food.food.re).toString() + "\n"
                                    reMeal.add(weight * food.food.re)
                                }
                                if (food.food.kar != null) cellKar += setTwoDigits(weight*food.food.kar).toString() + "\n"
                                if (food.food.mds != null) cellMds += setTwoDigits(weight*food.food.mds).toString() + "\n"
                                if (food.food.kr != null) cellKr += setTwoDigits(weight*food.food.kr).toString() + "\n"
                                if (food.food.te != null) cellTe += setTwoDigits(weight*food.food.te).toString() + "\n"
                                if (food.food.ok != null) cellOk += setTwoDigits(weight*food.food.ok).toString() + "\n"
                                if (food.food.ne != null) cellNe += setTwoDigits(weight*food.food.ne).toString() + "\n"
                                if (food.food.zn != null) cellZn += setTwoDigits(weight*food.food.zn).toString() + "\n"
                                if (food.food.cu != null) cellCu += setTwoDigits(weight*food.food.cu).toString() + "\n"
                                if (food.food.mn != null) cellMn += setTwoDigits(weight*food.food.mn).toString() + "\n"
                                if (food.food.se != null) cellSe += setTwoDigits(weight*food.food.se).toString() + "\n"
                                if (food.food.b5 != null) cellB5 += setTwoDigits(weight*food.food.b5).toString() + "\n"
                                if (food.food.b6 != null) cellB6 += setTwoDigits(weight*food.food.b6).toString() + "\n"
                                if (food.food.fol != null) cellFol += setTwoDigits(weight*food.food.fol).toString() + "\n"
                                if (food.food.b9 != null) cellB9 += setTwoDigits(weight*food.food.b9).toString() + "\n"
                                if (food.food.dfe != null) cellDfe += setTwoDigits(weight*food.food.dfe).toString() + "\n"
                                if (food.food.holin != null) cellHolin += setTwoDigits(weight*food.food.holin).toString() + "\n"
                                if (food.food.b12 != null) cellB12 += setTwoDigits(weight*food.food.b12).toString() + "\n"
                                if (food.food.ear != null) cellEar += setTwoDigits(weight*food.food.ear).toString() + "\n"
                                if (food.food.a_kar != null) cellAKar += setTwoDigits(weight*food.food.a_kar).toString() + "\n"
                                if (food.food.b_kript != null) cellBKript += setTwoDigits(weight*food.food.b_kript).toString() + "\n"
                                if (food.food.likopin != null) cellLikopin += setTwoDigits(weight*food.food.likopin).toString() + "\n"
                                if (food.food.lut_z != null) cellLutZ += setTwoDigits(weight*food.food.lut_z).toString() + "\n"
                                if (food.food.vit_e != null) cellVitE += setTwoDigits(weight*food.food.vit_e).toString() + "\n"
                                if (food.food.vit_d != null) cellVitD += setTwoDigits(weight*food.food.vit_d).toString() + "\n"
                                if (food.food.d_mezd != null) cellDMezd += setTwoDigits(weight*food.food.d_mezd).toString() + "\n"
                                if (food.food.vit_k != null) cellVitK += setTwoDigits(weight*food.food.vit_k).toString() + "\n"
                                if (food.food.mzhk != null) cellMzhk += setTwoDigits(weight*food.food.mzhk).toString() + "\n"
                                if (food.food.pzhk != null) cellPzhk += setTwoDigits(weight*food.food.pzhk).toString() + "\n"

                                if (food.food.w_1ed != null) cellW1Ed += setTwoDigits(weight*food.food.w_1ed).toString() + "\n"
                                if (food.food.op_1ed != null) cellOp1Ed += setTwoDigits(food.food.op_1ed).toString() + "\n"
                                if (food.food.w_2ed != null) cellW2Ed += setTwoDigits(food.food.w_2ed).toString() + "\n"
                                if (food.food.op_2ed != null) cellOp2Ed += setTwoDigits(food.food.op_2ed).toString() + "\n"
                                if (food.food.proc_pot != null) cellProcProt += setTwoDigits(weight*food.food.proc_pot).toString() + "\n"
                            }
                            val cellStrings = listOf(cellName, cellWeight, cellCarbs, cellFats,
                                cellProtein, cellKCal, cellGI, cellGL, "", cellWater, cellNzhk,
                                cellHol, cellPV, cellZola, cellNa, cellK, cellCa, cellMg, cellP,
                                cellFe, cellA, cellB1, cellB2, cellRr, cellC, cellRe, cellKar,
                                cellMds, cellKr, cellTe, cellOk, cellNe, cellZn, cellCu, cellMn,
                                cellSe, cellB5, cellB6, cellFol, cellB9, cellDfe, cellHolin,
                                cellB12, cellEar, cellAKar, cellBKript, cellLikopin, cellLutZ,
                                cellVitE, cellVitD, cellDMezd, cellVitK, cellMzhk, cellPzhk, "",
                                cellW1Ed, cellOp1Ed, cellW2Ed, cellOp2Ed, cellProcProt)
                            var k = 4
                            for (cellString in cellStrings) {
                                createCell(k).apply {
                                    setCellValue(cellString)
                                    cellStyle = styleNormal
                                }
                                k += 1
                            }
                            createCell(k+1).apply {
                                setCellValue(mealList[j].mealWithFoods.mealEntity.sugarLevel!!.toString())
                                cellStyle = styleNormal
                            }
                            createCell(k+2).apply {
                                setCellValue(mealList[j].mealWithFoods.mealEntity.sugarLevelPredicted!!.toString())
                                cellStyle = styleNormal
                            }

                            val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy")
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = mealList[j].recordEntity.dateSubmit!!
                            val dateString = formatter.format(calendar.time)

                            createCell(k+3).apply {
                                setCellValue(dateString)
                                cellStyle = styleNormal
                            }

                        }
                        j += 1
                        i += 1
                    }

                } catch (e: java.lang.IndexOutOfBoundsException) {

                } finally {
                    i -= 1
                    carbo.add(Pair(carboMeal.sum(),date.date))
                    prot.add(Pair(protMeal.sum(),date.date))
                    fat.add(Pair(fatMeal.sum(),date.date))
                    ec.add(Pair(ecMeal.sum(),date.date))
                    gi.add(Pair(giMeal.sum(),date.date))
                    water.add(Pair(waterMeal.sum(),date.date))
                    nzhk.add(Pair(nzhkMeal.sum(),date.date))
                    hol.add(Pair(holMeal.sum(),date.date))
                    pv.add(Pair(pvMeal.sum(),date.date))
                    zola.add(Pair(zolaMeal.sum(),date.date))
                    na.add(Pair(naMeal.sum(),date.date))
                    k.add(Pair(kMeal.sum(),date.date))
                    ca.add(Pair(caMeal.sum(),date.date))
                    mg.add(Pair(mgMeal.sum(),date.date))
                    p.add(Pair(pMeal.sum(),date.date))
                    fe.add(Pair(feMeal.sum(),date.date))
                    a.add(Pair(aMeal.sum(),date.date))
                    b1.add(Pair(b1Meal.sum(),date.date))
                    b2.add(Pair(b2Meal.sum(),date.date))
                    rr.add(Pair(rrMeal.sum(),date.date))
                    c.add(Pair(cMeal.sum(),date.date))
                    re.add(Pair(reMeal.sum(),date.date))

                    carboMeal.clear()
                    protMeal.clear()
                    fatMeal.clear()
                    ecMeal.clear()
                    giMeal.clear()
                    waterMeal.clear()
                    nzhkMeal.clear()
                    holMeal.clear()
                    pvMeal.clear()
                    zolaMeal.clear()
                    naMeal.clear()
                    kMeal.clear()
                    caMeal.clear()
                    mgMeal.clear()
                    pMeal.clear()
                    feMeal.clear()
                    aMeal.clear()
                    b1Meal.clear()
                    b2Meal.clear()
                    rrMeal.clear()
                    cMeal.clear()
                    reMeal.clear()
                }
            } else {
                sheet.createRow(i).createCell(1).apply {
                    setCellValue(date.date)
                    cellStyle = styleNormal
                }
            }
            i += 1
        }

        i += 1
        sheet.addMergedRegion(CellRangeAddress(i, i, 0, 67))
        sheet.createRow(i).createCell(0).apply {
            setCellValue("Приемы пищи")
            cellStyle = styleYellow
        }
        i += 1
        sheet.createRow(i).apply {
            var k = 1
            for (columnName in columnNames) {
                createCell(k).apply {
                    setCellValue(columnName)
                    cellStyle = styleYellow
                }
                k += 1
            }
        }

        i += 1
        j = 0
        for (date in dates) {
            sheet.createRow(i).apply {
                createCell(1).apply {
                    setCellValue(date.date)
                    cellStyle = styleNormal
                }
                if (carbo[j].second == date.date) {
                    val carboDay = mutableListOf<Double>()
                    val protDay = mutableListOf<Double>()
                    val fatDay = mutableListOf<Double>()
                    val ecDay = mutableListOf<Double>()
                    val giDay = mutableListOf<Double>()
                    val waterDay = mutableListOf<Double>()
                    val nzhkDay = mutableListOf<Double>()
                    val holDay = mutableListOf<Double>()
                    val pvDay = mutableListOf<Double>()
                    val zolaDay = mutableListOf<Double>()
                    val naDay = mutableListOf<Double>()
                    val kDay = mutableListOf<Double>()
                    val caDay = mutableListOf<Double>()
                    val mgDay = mutableListOf<Double>()
                    val pDay = mutableListOf<Double>()
                    val feDay = mutableListOf<Double>()
                    val aDay = mutableListOf<Double>()
                    val b1Day = mutableListOf<Double>()
                    val b2Day = mutableListOf<Double>()
                    val rrDay = mutableListOf<Double>()
                    val cDay = mutableListOf<Double>()
                    val reDay = mutableListOf<Double>()


                    try {
                        while (carbo[j].second == date.date) {
                            carboDay.add(carbo[j].first)
                            protDay.add(prot[j].first)
                            fatDay.add(fat[j].first)
                            ecDay.add(ec[j].first)
                            giDay.add(gi[j].first)
                            waterDay.add(water[j].first)
                            nzhkDay.add(nzhk[j].first)
                            holDay.add(hol[j].first)
                            pvDay.add(pv[j].first)
                            zolaDay.add(zola[j].first)
                            naDay.add(na[j].first)
                            kDay.add(k[j].first)
                            caDay.add(ca[j].first)
                            mgDay.add(mg[j].first)
                            pDay.add(p[j].first)
                            feDay.add(fe[j].first)
                            aDay.add(a[j].first)
                            b1Day.add(b1[j].first)
                            b2Day.add(b2[j].first)
                            rrDay.add(rr[j].first)
                            cDay.add(c[j].first)
                            reDay.add(re[j].first)
                            j += 1
                        }
                    }  catch (e: java.lang.IndexOutOfBoundsException) {

                    }

                    val avgs = listOf(
                        setTwoDigits(carboDay.sum()),
                        setTwoDigits(protDay.sum()),
                        setTwoDigits(fatDay.sum()),
                        setTwoDigits(ecDay.sum()),
                        setTwoDigits(giDay.sum()),
                        null,
                        null,
                        setTwoDigits(waterDay.sum()),
                        setTwoDigits(nzhkDay.sum()),
                        setTwoDigits(holDay.sum()),
                        setTwoDigits(pvDay.sum()),
                        setTwoDigits(zolaDay.sum()),
                        setTwoDigits(naDay.sum()),
                        setTwoDigits(kDay.sum()),
                        setTwoDigits(caDay.sum()),
                        setTwoDigits(mgDay.sum()),
                        setTwoDigits(pDay.sum()),
                        setTwoDigits(feDay.sum()),
                        setTwoDigits(aDay.sum()),
                        setTwoDigits(b1Day.sum()),
                        setTwoDigits(b2Day.sum()),
                        setTwoDigits(rrDay.sum()),
                        setTwoDigits(cDay.sum()),
                        setTwoDigits(reDay.sum()))
                    var k = 6
                    for (avg in avgs) {
                        if (avg != null) {
                            createCell(k).apply {
                                setCellValue(avg.toString())
                                cellStyle = styleNormal
                            }
                        }
                        k += 1
                    }
                }
            }
            i += 1
        }
        setBordersToMergedCells(sheet)

        return true
    }

    private suspend fun getFullDaysTable(sheet: XSSFSheet): Boolean {
        sheet.defaultColumnWidth = 10
        sheet.setColumnWidth(0, 5000)

        val fullDays = GlobalScope.async(Dispatchers.Default) {
            appDatabaseViewModel.readAllFullDays()
        }

        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 13))
        sheet.createRow(0).createCell(0).setCellValue(globalInfoString)

        sheet.createRow(1).createCell(0).apply {
            setCellValue("Список полных дней")
            cellStyle = styleYellow
        }
        sheet.createRow(2).createCell(0).apply {
            setCellValue("Дата")
            cellStyle = styleYellow
        }

        val fullDayList = fullDays.await()

        if (fullDayList.isNullOrEmpty()) return true

        var i = 1
        for (date in fullDayList) {
            sheet.getRow(2).createCell(i).apply {
                setCellValue(date)
                cellStyle = styleNormal
            }
            i += 1
        }

        setBordersToMergedCells(sheet)

        return true
    }

    private suspend fun getKetoneTable(sheet: XSSFSheet): Boolean {
        sheet.defaultColumnWidth = 18

        val ketoneRecords = GlobalScope.async(Dispatchers.Default) {
            appDatabaseViewModel.readAllKetoneRecords()
        }

        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 13))
        sheet.createRow(0).createCell(0).setCellValue(globalInfoString)
        sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 3))
        sheet.createRow(1).createCell(0).apply {
            setCellValue("Кетоны в моче")
            cellStyle = styleYellow
        }
        sheet.createRow(2).apply {
            if ((appType == "GDMRCT") or (appType == "GDM")) {
                createCell(0).apply {
                    setCellValue("Неделя беременности")
                    cellStyle = styleYellow
                }
            }
            createCell(1).apply {
                setCellValue("Дата")
                cellStyle = styleYellow
            }
            createCell(2).apply {
                setCellValue("Время")
                cellStyle = styleYellow
            }
            createCell(3).apply {
                setCellValue("Уровень, ммол/л")
                cellStyle = styleYellow
            }
        }

        val ketoneList = ketoneRecords.await()
        if (ketoneRecords.await().isNullOrEmpty()) return true
        val dates = getDates(ketoneList[0].recordEntity.date!!, ketoneList.last().recordEntity.date!!)

        var i = 3
        var j = 0
        for (date in dates) {
            sheet.createRow(i).apply {
                createCell(1).apply {
                    setCellValue(date.date)
                    cellStyle = styleNormal
                }
                try {
                    while (ketoneList[j].recordEntity.date == date.date) {
                        var cellTime: String
                        var cellLevel: String
                        if (getCell(2) != null) {
                            cellTime = getCell(2).stringCellValue + "\n" + ketoneList[j].recordEntity.time
                            cellLevel = getCell(3).stringCellValue + "\n" + ketoneList[j].ketoneEntity.ketone.toString()
                        } else {
                            cellTime = ketoneList[j].recordEntity.time!!
                            cellLevel = ketoneList[j].ketoneEntity.ketone.toString()
                        }
                        createCell(2).apply {
                            setCellValue(cellTime)
                            cellStyle = styleNormal
                        }
                        createCell(3).apply {
                            setCellValue(cellLevel)
                            cellStyle = styleNormal
                        }
                        j += 1
                    }
                } catch (e: java.lang.IndexOutOfBoundsException) { }
            }
            i += 1
        }

        setBordersToMergedCells(sheet)

        return true
    }

    private suspend fun getWeightTable(sheet: XSSFSheet): Boolean {
        sheet.defaultColumnWidth = 10

        val weightRecords = GlobalScope.async(Dispatchers.Default) {
            appDatabaseViewModel.readAllWeightRecords()
        }

        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 13))
        sheet.createRow(0).createCell(0).setCellValue(globalInfoString)
        sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 3))
        sheet.createRow(1).createCell(0).apply {
            setCellValue("Физическая нагрузка")
            cellStyle = styleYellow
        }
        sheet.createRow(2).apply {
            if ((appType == "GDMRCT") or (appType == "GDM")) {
                createCell(0).apply {
                    setCellValue("Неделя беременности")
                    cellStyle = styleYellow
                }
            }
            createCell(1).apply {
                setCellValue("Дата")
                cellStyle = styleYellow
            }
            createCell(2).apply {
                setCellValue("Время")
                cellStyle = styleYellow
            }
            createCell(3).apply {
                setCellValue("Вес, кг")
                cellStyle = styleYellow
            }
        }

        val weightList = weightRecords.await().toList()
        if (weightList.isNullOrEmpty()) return true
        val dates = getDates(weightList[0].recordEntity.date!!, weightList.last().recordEntity.date!!)

        var i = 3
        var j = 0
        for (date in dates) {
            sheet.createRow(i).apply {
                createCell(1).apply {
                    setCellValue(date.date)
                    cellStyle = styleNormal
                }
                try {
                    while (weightList[j].recordEntity.date == date.date) {
                        var cellTime: String
                        var cellWeight: String
                        if (getCell(2) != null) {
                            cellTime = getCell(2).stringCellValue + "\n" + weightList[j].recordEntity.time
                            cellWeight = getCell(3).stringCellValue + "\n" + weightList[j].weightEntity.weight.toString()
                        } else {
                            cellTime = weightList[j].recordEntity.time!!
                            cellWeight = weightList[j].weightEntity.weight.toString()
                        }
                        createCell(2).apply {
                            setCellValue(cellTime)
                            cellStyle = styleNormal
                        }
                        createCell(3).apply {
                            setCellValue(cellWeight)
                            cellStyle = styleNormal
                        }
                        j += 1
                    }
                } catch (e: java.lang.IndexOutOfBoundsException) { }
            }
            i += 1
        }

        setBordersToMergedCells(sheet)

        return true
    }

    private suspend fun getWorkoutAndSleepTable(sheet: XSSFSheet): Boolean {

        sheet.defaultColumnWidth = 18

        val workoutRecords = GlobalScope.async(Dispatchers.Default) {
            appDatabaseViewModel.readAllWorkoutRecords()
        }
        val sleepRecords = GlobalScope.async(Dispatchers.Default) {
            appDatabaseViewModel.readAllSleepRecords()
        }

        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 13))
        sheet.createRow(0).createCell(0).setCellValue(globalInfoString)
        sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 4))
        sheet.addMergedRegion(CellRangeAddress(1, 1, 6, 7))
        sheet.createRow(1).apply {
            createCell(0).apply {
                setCellValue("Физическая нагрузка")
                cellStyle = styleYellow
            }
            createCell(6).apply {
                setCellValue("Сон")
                cellStyle = styleYellow
            }
        }
        sheet.createRow(2).apply {
            if ((appType == "GDMRCT") or (appType == "GDM")) {
                createCell(0).apply {
                    setCellValue("Неделя беременности")
                    cellStyle = styleYellow
                }
            }
            createCell(1).apply {
                setCellValue("Дата")
                cellStyle = styleYellow
            }
            createCell(2).apply {
                setCellValue("Время")
                cellStyle = styleYellow
            }
            createCell(3).apply {
                setCellValue("Длительность, мин.")
                cellStyle = styleYellow
            }
            createCell(4).apply {
                setCellValue("Тип нагрузки")
                cellStyle = styleYellow
            }

            createCell(6).apply {
                setCellValue("Время")
                cellStyle = styleYellow
            }
            createCell(7).apply {
                setCellValue("Длительность, ч.")
                cellStyle = styleYellow
            }
        }
        val workoutList = workoutRecords.await().toList()
        val sleepList = sleepRecords.await().toList()
        if (workoutList.isNullOrEmpty() and sleepList.isNullOrEmpty()) return true

        var minDate: String
        var maxDate: String

        if (workoutList.isNullOrEmpty() and !sleepList.isNullOrEmpty()) {
            minDate = sleepList[0].recordEntity.date!!
            maxDate = sleepList.last().recordEntity.date!!
        } else if (!workoutList.isNullOrEmpty() and sleepList.isNullOrEmpty()) {
            minDate = workoutList[0].recordEntity.date!!
            maxDate = workoutList.last().recordEntity.date!!
        } else
        {
            minDate = if (workoutList[0].recordEntity.dateInMilli!! < sleepList[0].recordEntity.dateInMilli!!)
                workoutList[0].recordEntity.date!! else sleepList[0].recordEntity.date!!
            maxDate = if (workoutList.last().recordEntity.dateInMilli!! > sleepList.last().recordEntity.dateInMilli!!)
                workoutList.last().recordEntity.date!! else sleepList.last().recordEntity.date!!
        }

        val dates = getDates(minDate, maxDate)
        var i = 3
        var j = 0
        var k = 0
        for (date in dates) {
            sheet.createRow(i).apply {
                createCell(1).apply {
                    setCellValue(date.date)
                    cellStyle = styleNormal
                }
                try {
                    while (workoutList[j].recordEntity.date == date.date) {
                        var cellTime: String
                        var cellDuration: String
                        var cellType: String
                        if (getCell(2) != null){
                            cellTime = getCell(2).stringCellValue + "\n" + workoutList[j].recordEntity.time
                            cellDuration = getCell(3).stringCellValue + "\n" + workoutList[j].workoutEntity.duration.toString()
                            cellType = getCell(4).stringCellValue + "\n" + workoutList[j].workoutEntity.type.toString()
                        } else {
                            cellTime = workoutList[j].recordEntity.time!!
                            cellDuration = workoutList[j].workoutEntity.duration.toString()
                            cellType = workoutList[j].workoutEntity.type.toString()
                        }
                        createCell(2).apply {
                            setCellValue(cellTime)
                            cellStyle = styleNormal
                        }
                        createCell(3).apply {
                            setCellValue(cellDuration)
                            cellStyle = styleNormal
                        }
                        createCell(4).apply {
                            setCellValue(cellType)
                            cellStyle = styleNormal
                        }
                        j += 1
                    }
                } catch (e: java.lang.IndexOutOfBoundsException) { }
                try {
                    while (sleepList[k].recordEntity.date == date.date) {
                        var cellTime: String
                        var cellDuration: String
                        if (getCell(2) != null){
                            cellTime = getCell(2).stringCellValue + "\n" + sleepList[k].recordEntity.time
                            cellDuration = getCell(3).stringCellValue + "\n" + sleepList[k].sleepEntity.duration.toString()
                        } else {
                            cellTime = sleepList[k].recordEntity.time!!
                            cellDuration = sleepList[k].sleepEntity.duration.toString()
                        }
                        createCell(6).apply {
                            setCellValue(cellTime)
                            cellStyle = styleNormal
                        }
                        createCell(7).apply {
                            setCellValue(cellDuration)
                            cellStyle = styleNormal
                        }
                        k += 1
                    }
                } catch (e: java.lang.IndexOutOfBoundsException) { }
            }
            i += 1
        }

        setBordersToMergedCells(sheet)
        return true
    }

    private suspend fun getSugarLevelAndInsulinTable(sheet: XSSFSheet): Boolean{

        sheet.defaultColumnWidth = 9
        sheet.setColumnWidth(16, 5000)
        sheet.setColumnWidth(19, 5000)
        sheet.setColumnWidth(22, 5000)
        sheet.setColumnWidth(25, 5000)
        sheet.setColumnWidth(28, 5000)

        val sugarLevelRecords = GlobalScope.async(Dispatchers.Default) {
            appDatabaseViewModel.readAllSugarLevelRecords()
        }
        val insulinRecords = GlobalScope.async(Dispatchers.Default) {
            appDatabaseViewModel.readAllInsulinRecords()
        }

        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 13))
        sheet.createRow(0).createCell(0).setCellValue(globalInfoString)
        sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 13))
        sheet.addMergedRegion(CellRangeAddress(1, 1, 15, 32))
        sheet.createRow(1).apply {
            createCell(0).apply {
                setCellValue("Измерение сахара")
                cellStyle = styleYellow
            }
            createCell(15).apply {
                setCellValue("Инъекции инсулина")
                cellStyle = styleYellow
            }
        }

        val listRowNames = mutableListOf(
            "Натощак", "После завтрака", "После обеда", "После ужина",
            "Дополнительно", "При родах"
        )
        sheet.createRow(2).apply {

            if ((appType == "GDMRCT") or (appType == "GDM")) {
                createCell(0).apply {
                    setCellValue("Неделя беременности")
                    cellStyle = styleYellow
                }
            }

            createCell(1).apply {
                setCellValue("Дата")
                cellStyle = styleYellow
            }
            var i = 1
            for (rowName in listRowNames) {
                sheet.addMergedRegion(CellRangeAddress(2, 2, i * 2, i * 2 + 1))
                createCell(2 * i).apply {
                    setCellValue(rowName)
                    cellStyle = styleYellow
                }
                i += 1
            }
            listRowNames.removeLast()
            listRowNames.add("Левемир")
            i = 1
            for (rowName in listRowNames) {
                sheet.addMergedRegion(CellRangeAddress(2, 2, 12 + i * 3, 12 + i * 3 + 2))
                createCell(12 + 3 * i).apply {
                    setCellValue(rowName)
                    cellStyle = styleYellow
                }
                i += 1
            }
        }

        val sugarLevelColumnIndex = mutableMapOf(
            "Натощак" to 2,
            "После завтрака" to 4,
            "После обеда" to 6,
            "После ужина" to 8,
            "При родах" to 12,
            "Дополнительно" to 10
        )
        val insulinColumnIndex = mutableMapOf(
            "Натощак" to 15,
            "После завтрака" to 18,
            "После обеда" to 21,
            "После ужина" to 24,
            "Дополнительно" to 27
        )
        val sugarLevelList = sugarLevelRecords.await().toList()
        val insulinList = insulinRecords.await().toList()
        if (sugarLevelList.isNullOrEmpty() and insulinList.isNullOrEmpty()) return true

        var minDate: String
        var maxDate: String

        if (sugarLevelList.isNullOrEmpty() and !insulinList.isNullOrEmpty()) {
            minDate = insulinList[0].recordEntity.date!!
            maxDate = insulinList.last().recordEntity.date!!
        } else if (!sugarLevelList.isNullOrEmpty() and insulinList.isNullOrEmpty()) {
            minDate = sugarLevelList[0].recordEntity.date!!
            maxDate = sugarLevelList.last().recordEntity.date!!
        } else
        {
            minDate = if (sugarLevelList[0].recordEntity.dateInMilli!! < insulinList[0].recordEntity.dateInMilli!!)
                sugarLevelList[0].recordEntity.date!! else insulinList[0].recordEntity.date!!
            maxDate = if (sugarLevelList.last().recordEntity.dateInMilli!! > insulinList.last().recordEntity.dateInMilli!!)
                sugarLevelList.last().recordEntity.date!! else insulinList.last().recordEntity.date!!
        }

        val dates = getDates(minDate, maxDate)
        var i = 3
        var j = 0
        var k = 0
        val sl1 = mutableListOf<Double>()
        val sl2 = mutableListOf<Double>()
        val sl3 = mutableListOf<Double>()
        val sl4 = mutableListOf<Double>()
        val sl5 = mutableListOf<Double>()
        for (date in dates) {
            sheet.createRow(i).apply {

                if ((appType == "GDMRCT") or (appType == "GDM")) {
                    createCell(0).apply {
                        setCellValue(date.week)
                        cellStyle = if (date.bool) styleBlue else styleSeaGreen
                    }
                }

                createCell(1).apply {
                    setCellValue(date.date)
                    cellStyle = styleNormal
                }
                try {
                    while (sugarLevelList[j].recordEntity.date == date.date) {
                        val columnIndex = sugarLevelColumnIndex[sugarLevelList[j].sugarLevelEntity.preferences]
                        val sugarLevel = sugarLevelList[j].sugarLevelEntity.sugarLevel!!
                        when (columnIndex) {
                            2 -> sl1.add(sugarLevel)
                            4 -> sl2.add(sugarLevel)
                            6 -> sl3.add(sugarLevel)
                            8 -> sl4.add(sugarLevel)
                            10 -> sl5.add(sugarLevel)
                        }

                        var cellValue: String
                        var cellTime: String
                        if (getCell(columnIndex!!) != null){
                            cellValue = getCell(columnIndex).stringCellValue + "\n" + sugarLevel.toString()
                            cellTime = getCell(columnIndex+1).stringCellValue + "\n" + sugarLevelList[j].recordEntity.time
                        } else {
                            cellValue = sugarLevel.toString()
                            cellTime = sugarLevelList[j].recordEntity.time!!
                        }
                        createCell(columnIndex).apply {
                            setCellValue(cellValue)
                            cellStyle = if (sugarLevel > 6.8) styleRed
                            else if (sugarLevel < 4) styleBlue
                            else styleNormal
                        }
                        createCell(columnIndex + 1).apply {
                            setCellValue(cellTime)
                            cellStyle = styleNormal
                        }
                        j += 1
                    }
                } catch (e: java.lang.IndexOutOfBoundsException) { }
                try {
                    while (insulinList[k].recordEntity.date == date.date) {
                        val columnIndex = insulinColumnIndex[insulinList[k].insulinEntity.preferences]
                        val insulin = insulinList[k].insulinEntity.insulin
                        var cellValue: String
                        var cellType: String
                        var cellTime: String
                        if (getCell(columnIndex!!) != null){
                            cellValue = getCell(columnIndex).stringCellValue + "\n" + insulin.toString()
                            cellType = getCell(columnIndex+1).stringCellValue + "\n" + insulinList[k].insulinEntity.type
                            cellTime = getCell(columnIndex+2).stringCellValue + "\n" + insulinList[k].recordEntity.time
                        } else {
                            cellValue = insulin.toString()
                            cellType = insulinList[k].insulinEntity.type!!
                            cellTime = insulinList[k].recordEntity.time!!
                        }
                        createCell(columnIndex).apply {
                            setCellValue(cellValue)
                            cellStyle = styleNormal
                        }
                        createCell(columnIndex + 1).apply {
                            setCellValue(cellType)
                            cellStyle = styleNormal

                        }
                        createCell(columnIndex + 2).apply {
                            setCellValue(cellTime)
                            cellStyle = styleNormal
                        }
                        k += 1
                    }
                } catch (e: java.lang.IndexOutOfBoundsException) { }
            }
            i += 1
        }

        sheet.createRow(i).apply {
            createCell(1).apply {
                setCellValue("Cреднее значение")
                cellStyle = styleNormal
            }
            if (sl1.isNotEmpty()) {
                createCell(2).apply {
                    setCellValue(sl1.average().toString())
                    cellStyle = styleNormal
                }
            }

            if (sl2.isNotEmpty()) {
                createCell(4).apply {
                    setCellValue(sl2.average().toString())
                    cellStyle = styleNormal
                }
            }

            if (sl3.isNotEmpty()) {
                createCell(6).apply {
                    setCellValue(sl3.average().toString())
                    cellStyle = styleNormal
                }
            }

            if (sl4.isNotEmpty()) {
                createCell(8).apply {
                    setCellValue(sl4.average().toString())
                    cellStyle = styleNormal
                }
            }

            if (sl5.isNotEmpty()) {
                createCell(10).apply {
                    setCellValue(sl5.average().toString())
                    cellStyle = styleNormal
                }
            }
        }

        setBordersToMergedCells(sheet)
        return true
    }

    private fun getDates(firstDate: String, lastDate: String): MutableList<ExportDate> {
        val dates = mutableListOf<ExportDate>()
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val date1 = formatter.parse(firstDate)
        val date2 = formatter.parse(lastDate)

        val cal1 = Calendar.getInstance()
        cal1.time = date1!!

        val cal2 = Calendar.getInstance()
        cal2.time = date2!!

        var dayOfTheWeek = 0
        var pregnancyWeek = 0
        var bool = false

        if ((appType == "GDMRCT") or (appType == "GDM")) {
            val datePregnancyStart = sharedPreferences.getString("PREGNANCY_DATE","01.01.2000")!!

            val dateP = formatter.parse(datePregnancyStart)
            val calP = Calendar.getInstance()
            calP.time = dateP!!
            val daysBetween = ((cal1.timeInMillis - calP.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
            pregnancyWeek = (daysBetween / 7) + 1
            dayOfTheWeek = daysBetween % 7
            if (dayOfTheWeek == 0) dayOfTheWeek = -1
        }

        while(!cal1.after(cal2))
        {
            val date = formatter.format(cal1.time)
            if (dayOfTheWeek == 6) {
                dayOfTheWeek = -1
                pregnancyWeek += 1
                bool = !bool
            }
            dayOfTheWeek += 1
            dates.add(ExportDate(date,pregnancyWeek.toString(),bool))
            cal1.add(Calendar.DATE, 1)
        }
        return dates
    }

    private fun setBordersToMergedCells(sheet: Sheet) {
        val mergedRegions: List<CellRangeAddress> = sheet.mergedRegions
        for (rangeAddress in mergedRegions) {
            RegionUtil.setBorderTop(BorderStyle.THIN, rangeAddress, sheet)
            RegionUtil.setBorderLeft(BorderStyle.THIN, rangeAddress, sheet)
            RegionUtil.setBorderRight(BorderStyle.THIN, rangeAddress, sheet)
            RegionUtil.setBorderBottom(BorderStyle.THIN, rangeAddress, sheet)
        }
    }
}