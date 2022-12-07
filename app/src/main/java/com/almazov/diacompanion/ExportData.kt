package com.almazov.diacompanion

import android.content.ActivityNotFoundException
import android.content.Context
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
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class ExportData : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel

    private lateinit var globalInfoString: String

    private lateinit var styleRed: XSSFCellStyle
    private lateinit var styleYellow: XSSFCellStyle
    private lateinit var styleBlue: XSSFCellStyle
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
        btn_export_to_slxs.setOnClickListener{
            createXmlFile()
        }

        btn_export_to_doctor.setOnClickListener{
            findNavController().popBackStack()
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

        val name = sharedPreferences.getString("NAME","Имя")
        val secondName = sharedPreferences.getString("SECOND_NAME","Фамилия")
        val patronymic = sharedPreferences.getString("PATRONYMIC","Отчество")
        val attendingDoctor = sharedPreferences.getString("ATTENDING_DOCTOR","Лечащий врач")
        val birthDate = sharedPreferences.getLong("BIRTH_DATE",0)
        val appType = sharedPreferences.getInt("APP_TYPE",1)
        val appTypeString = appType.toString()
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val birthDateString = formatter.format(Date(birthDate))
        globalInfoString = "Пациент: $secondName $name $patronymic;   " +
                "Дата рождения: $birthDateString;   Лечащий врач: $attendingDoctor;   " +
                "Программа: DiaCompanion Android $appTypeString"

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

                /*val uriPath = Uri.parse(path)
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
                }*/
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
            "Углеводы (г)", "Белки (г)", "Жиры (г)", "Энерг. ценность (Ккал)",
            "Пищевые волокна (г)", "ГИ", "ГН","", "Вода (г)", "НЖК (г)", "Холестерин (мг)",
            "Зола (г)", "Натрий (мг)", "Калий (мг)", "Кальций (мг)", "Магний (мг)", "Фосфор (мг)",
            "Железо (мг)", "Ретинол (мкг)", "Тиамин (мг)", "Рибофлавин (мг)", "Ниацин (мг)",
            "Витамин C (мг)", "Ретиновый эквивалент (мкг)", "", "Бета-каротин (мкг)",
            "Сахар, общее содержание (г)", "Крахмал (г)", "Токоферолэквивалент (мг)",
            "Органические кислоты (г)", "Ниациновый эквивалент (мг)", "Цинк (мг)", "Медь (мг)",
            "Марганец (мг)", "Селен (мкг)", "Пантотеновая кислота (мг)", "Витамин B6 (мг)",
            "Фолаты общ. (мкг)", "Фолиевая кислота (мкг)", "Фолаты ДФЭ (мкг)", "Холин общ. (мкг)",
            "Витамин B12 (мг)", "Витамин A (ЭАР)", "Альфа-каротин (мкг)",
            "Криптоксантин бета (мкг)", "Ликопин (мкг)", "Лютеин + Гексаксантин (мкг)",
            "Витамин E (мг)", "Витамин D (мкг)", "Витамин D (межд.ед.)", "Витамин K (мкг)",
            "Мононенасыщенные жирные кислоты (г)", "Полиненасыщенные жирные кислоты (г)", "",
            "Вес перв. ед. изм.", "Описание перв. ед. изм.", "Вес второй ед. изм",
            "Опис. второй ед. изм.", "Процент потерь, %", "", "УСК до еды", "Прогноз УСК"
        )
        sheet.createRow(2).apply {
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

        val ketoneList = ketoneRecords.await().toList()
        if (ketoneList.isNullOrEmpty()) return true
        val dates = getDates(ketoneList[0].first.date!!, ketoneList.last().first.date!!)

        var i = 3
        var j = 0
        for (date in dates) {
            sheet.createRow(i).apply {
                createCell(1).apply {
                    setCellValue(date)
                    cellStyle = styleNormal
                }
                try {
                    while (ketoneList[j].first.date == date) {
                        var cellTime: String
                        var cellLevel: String
                        if (getCell(2) != null) {
                            cellTime = getCell(2).stringCellValue + "\n" + ketoneList[j].first.time
                            cellLevel = getCell(3).stringCellValue + "\n" + ketoneList[j].second.ketone.toString()
                        } else {
                            cellTime = ketoneList[j].first.time!!
                            cellLevel = ketoneList[j].second.ketone.toString()
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
        val dates = getDates(weightList[0].first.date!!, weightList.last().first.date!!)

        var i = 3
        var j = 0
        for (date in dates) {
            sheet.createRow(i).apply {
                createCell(1).apply {
                    setCellValue(date)
                    cellStyle = styleNormal
                }
                try {
                    while (weightList[j].first.date == date) {
                        var cellTime: String
                        var cellWeight: String
                        if (getCell(2) != null) {
                            cellTime = getCell(2).stringCellValue + "\n" + weightList[j].first.time
                            cellWeight = getCell(3).stringCellValue + "\n" + weightList[j].second.weight.toString()
                        } else {
                            cellTime = weightList[j].first.time!!
                            cellWeight = weightList[j].second.weight.toString()
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
            minDate = sleepList[0].first.date!!
            maxDate = sleepList.last().first.date!!
        } else if (!workoutList.isNullOrEmpty() and sleepList.isNullOrEmpty()) {
            minDate = workoutList[0].first.date!!
            maxDate = workoutList.last().first.date!!
        } else
        {
            minDate = if (workoutList[0].first.dateInMilli!! < sleepList[0].first.dateInMilli!!)
                workoutList[0].first.date!! else sleepList[0].first.date!!
            maxDate = if (workoutList.last().first.dateInMilli!! > sleepList.last().first.dateInMilli!!)
                workoutList.last().first.date!! else sleepList.last().first.date!!
        }

        val dates = getDates(minDate, maxDate)
        var i = 3
        var j = 0
        var k = 0
        for (date in dates) {
            sheet.createRow(i).apply {
                createCell(1).apply {
                    setCellValue(date)
                    cellStyle = styleNormal
                }
                try {
                    while (workoutList[j].first.date == date) {
                        var cellTime: String
                        var cellDuration: String
                        var cellType: String
                        if (getCell(2) != null){
                            cellTime = getCell(2).stringCellValue + "\n" + workoutList[j].first.time
                            cellDuration = getCell(3).stringCellValue + "\n" + workoutList[j].second.duration.toString()
                            cellType = getCell(4).stringCellValue + "\n" + workoutList[j].second.type.toString()
                        } else {
                            cellTime = workoutList[j].first.time!!
                            cellDuration = workoutList[j].second.duration.toString()
                            cellType = workoutList[j].second.type.toString()
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
                    while (sleepList[k].first.date == date) {
                        var cellTime: String
                        var cellDuration: String
                        if (getCell(2) != null){
                            cellTime = getCell(2).stringCellValue + "\n" + sleepList[k].first.time
                            cellDuration = getCell(3).stringCellValue + "\n" + sleepList[k].second.duration.toString()
                        } else {
                            cellTime = sleepList[k].first.time!!
                            cellDuration = sleepList[k].second.duration.toString()
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
            minDate = insulinList[0].first.date!!
            maxDate = insulinList.last().first.date!!
        } else if (!sugarLevelList.isNullOrEmpty() and insulinList.isNullOrEmpty()) {
            minDate = sugarLevelList[0].first.date!!
            maxDate = sugarLevelList.last().first.date!!
        } else
        {
            minDate = if (sugarLevelList[0].first.dateInMilli!! < insulinList[0].first.dateInMilli!!)
                sugarLevelList[0].first.date!! else insulinList[0].first.date!!
            maxDate = if (sugarLevelList.last().first.dateInMilli!! > insulinList.last().first.dateInMilli!!)
                sugarLevelList.last().first.date!! else insulinList.last().first.date!!
        }

        val dates = getDates(minDate, maxDate)
        var i = 3
        var j = 0
        var k = 0
        for (date in dates) {
            sheet.createRow(i).apply {
                createCell(1).apply {
                    setCellValue(date)
                    cellStyle = styleNormal
                }
                try {
                    while (sugarLevelList[j].first.date == date) {
                        val columnIndex = sugarLevelColumnIndex[sugarLevelList[j].second.preferences]
                        var cellValue: String
                        var cellTime: String
                        if (getCell(columnIndex!!) != null){
                            cellValue = getCell(columnIndex).stringCellValue + "\n" + sugarLevelList[j].second.sugarLevel.toString()
                            cellTime = getCell(columnIndex+1).stringCellValue + "\n" + sugarLevelList[j].first.time
                        } else {
                            cellValue = sugarLevelList[j].second.sugarLevel.toString()
                            cellTime = sugarLevelList[j].first.time!!
                        }
                        createCell(columnIndex).apply {
                            setCellValue(cellValue)
                            cellStyle = if (sugarLevelList[j].second.sugarLevel!! > 6.8) styleRed
                            else if (sugarLevelList[j].second.sugarLevel!! < 4) styleBlue
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
                    while (insulinList[k].first.date == date) {
                        val columnIndex = insulinColumnIndex[insulinList[k].second.preferences]
                        var cellValue: String
                        var cellType: String
                        var cellTime: String
                        if (getCell(columnIndex!!) != null){
                            cellValue = getCell(columnIndex).stringCellValue + "\n" + insulinList[k].second.insulin.toString()
                            cellType = getCell(columnIndex+1).stringCellValue + "\n" + insulinList[k].second.type
                            cellTime = getCell(columnIndex+2).stringCellValue + "\n" + insulinList[k].first.time
                        } else {
                            cellValue = insulinList[k].second.insulin.toString()
                            cellType = insulinList[k].second.type!!
                            cellTime = insulinList[k].first.time!!
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

        setBordersToMergedCells(sheet)
        return true
    }

    private fun getDates(firstDate: String, lastDate: String): MutableList<String> {
        val dates = mutableListOf<String>()
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val date1 = formatter.parse(firstDate)
        val date2 = formatter.parse(lastDate)

        val cal1 = Calendar.getInstance()
        cal1.time = date1!!

        val cal2 = Calendar.getInstance()
        cal2.time = date2!!

        while(!cal1.after(cal2))
        {
            dates.add(formatter.format(cal1.time))
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