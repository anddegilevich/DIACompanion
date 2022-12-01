package com.almazov.diacompanion

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.almazov.diacompanion.data.AppDatabaseViewModel
import kotlinx.android.synthetic.main.fragment_export_data.*
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.sql.Date
import java.text.SimpleDateFormat


class ExportData : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel

    private lateinit var globalInfoString: String

    private lateinit var styleRed: XSSFCellStyle
    private lateinit var styleYellow: XSSFCellStyle
    private lateinit var styleBlue: XSSFCellStyle


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_export_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sharedPreferences = requireContext().getSharedPreferences("SharedPreferences",
            Context.MODE_PRIVATE)
        btn_export_to_slxs.setOnClickListener{
            createXmlFile()
            findNavController().popBackStack()
        }

        btn_export_to_doctor.setOnClickListener{
            findNavController().popBackStack()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun createXmlFile() {

        val xlWb = XSSFWorkbook()
        styleRed = xlWb.createCellStyle().apply {
            fillForegroundColor = IndexedColors.RED.getIndex()
            fillPattern = FillPatternType.SOLID_FOREGROUND
        }

        styleYellow = xlWb.createCellStyle().apply {
            fillForegroundColor = IndexedColors.YELLOW.getIndex()
            fillPattern = FillPatternType.SOLID_FOREGROUND
        }

        styleBlue = xlWb.createCellStyle().apply {
            fillForegroundColor = IndexedColors.BLUE.getIndex()
            fillPattern = FillPatternType.SOLID_FOREGROUND
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

        val xlWsSugarLevel = xlWb.createSheet()
        getSugarLevelAndInsulinTable(xlWsSugarLevel)

        val result = FileOutputStream(requireContext().getDatabasePath("exportTest.xlsx").path)

        xlWb.write(result)
        xlWb.close()
    }

    private fun getSugarLevelAndInsulinTable(sheet: XSSFSheet) {
        sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 13))
        sheet.createRow(0).createCell(0).setCellValue(globalInfoString)
        sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 13))
        sheet.createRow(1).createCell(0).apply {
            setCellValue("Измерение сахара")
            cellStyle = styleYellow}
        sheet.createRow(2).createCell(1).apply {
            setCellValue("Дата")
            cellStyle = styleYellow}
        val listRowNames = listOf("Натощак", "После завтрака", "После обеда", "После ужина",
            "Дополнительно", "При родах")

        sheet.createRow(2).apply {
            var i = 1
            for (rowName in listRowNames) {
                sheet.addMergedRegion(CellRangeAddress(2, 2, i*2, i*2+1))
                createCell(2*i).apply {
                    setCellValue(rowName)
                    cellStyle = styleYellow
                }
                i += 1
            }
        }
    }

}