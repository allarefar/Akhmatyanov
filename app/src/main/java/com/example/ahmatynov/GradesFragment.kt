package com.example.ahmatynov

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class GradesFragment : Fragment() {

    private lateinit var storageReference: StorageReference
    private lateinit var tableLayout: TableLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_grades, container, false)

        // Получаем ссылку на Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        // Инициализируем TableLayout
        tableLayout = view.findViewById(R.id.tableLayout)

        // Загрузить таблицу Excel
        loadExcelFile()

        return view
    }

    private fun loadExcelFile() {
        val userClass = getUserClassFromSharedPreferences()

        // Путь к файлу в Firebase Storage
        val filePath = "table/$userClass/table.xlsx"
        Log.d("GradesFragment", "Путь к файлу: $filePath")

        val fileRef = storageReference.child(filePath)

        fileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            displayExcelFile(bytes)
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Ошибка загрузки файла: ${exception.message}", Toast.LENGTH_LONG).show()
            Log.e("GradesFragment", "Ошибка загрузки файла", exception)
        }
    }

    private fun displayExcelFile(bytes: ByteArray) {
        try {
            val inputStream: InputStream = bytes.inputStream()
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)

            for (row in sheet) {
                val tableRow = TableRow(context)
                tableRow.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )

                for (cell in row) {
                    val cellTextView = TextView(context)
                    cellTextView.text = when (cell.cellType) {
                        CellType.NUMERIC -> {
                            if (DateUtil.isCellDateFormatted(cell)) {
                                cell.dateCellValue.toString()
                            } else {
                                cell.numericCellValue.toInt().toString()
                            }
                        }
                        CellType.STRING -> cell.stringCellValue
                        CellType.BOOLEAN -> cell.booleanCellValue.toString()
                        CellType.FORMULA -> cell.cellFormula
                        else -> ""
                    }
                    cellTextView.setPadding(8, 8, 8, 8)
                    cellTextView.background = resources.getDrawable(R.drawable.cell_border, null)
                    tableRow.addView(cellTextView)
                }
                tableLayout.addView(tableRow)
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка отображения файла: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("GradesFragment", "Ошибка отображения файла", e)
        }
    }

    private fun getUserClassFromSharedPreferences(): String {
        val sharedPreferences = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_CLASS", "") ?: ""
    }
}