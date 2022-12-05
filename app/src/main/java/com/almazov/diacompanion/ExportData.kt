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
        sharedPreferences = requireContext().getSharedPreferences("SharedPreferences",
            Context.MODE_PRIVATE)
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

        val xlWsSugarLevel = xlWb.createSheet("Уровень сахара и инсулин")
        GlobalScope.launch(Dispatchers.Main) {
            val sugarLevelSheetCompleted = GlobalScope.async(Dispatchers.Default) {
                getSugarLevelAndInsulinTable(xlWsSugarLevel)
            }
            if (sugarLevelSheetCompleted.await()) {
                val path = requireContext().getDatabasePath("exportTest.xlsx").path
                val table = FileOutputStream(path)

                xlWb.write(table)
                xlWb.close()

                val uriPath = Uri.parse(path)


                // Show Excel file
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
                findNavController().popBackStack()
            }
        }

    }

    private suspend fun getSugarLevelAndInsulinTable(sheet: XSSFSheet): Boolean{

        sheet.defaultColumnWidth = 9

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
        sheet.setColumnWidth(16, 5000)
        sheet.setColumnWidth(19, 5000)
        sheet.setColumnWidth(22, 5000)
        sheet.setColumnWidth(25, 5000)
        sheet.setColumnWidth(28, 5000)
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