package com.example.ahmatynov.food

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
import com.example.ahmatynov.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.otaliastudios.zoom.ZoomLayout
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class FoodFragment : Fragment() {

    private lateinit var storageReference: StorageReference
    private lateinit var tableLayout: TableLayout
    private lateinit var zoomLayout: ZoomLayout
    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fileName = it.getString("fileName")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_food, container, false)

        // Получаем ссылку на Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        // Инициализируем элементы UI
        tableLayout = view.findViewById(R.id.tableLayout)
        zoomLayout = view.findViewById(R.id.zoom_layout)

        // Загружаем файл Excel
        fileName?.let {
            loadExcelFile(it)
        }

        return view
    }

    private fun loadExcelFile(fileName: String) {
        val userClass = getUserClassFromSharedPreferences()
        val filePath = "food/$fileName"
        Log.d("FoodFragment", "Путь к файлу: $filePath")

        val fileRef = storageReference.child(filePath)

        fileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            displayExcelFile(bytes)
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Ошибка загрузки файла: ${exception.message}", Toast.LENGTH_LONG).show()
            Log.e("FoodFragment", "Ошибка загрузки файла", exception)
        }
    }

    private fun displayExcelFile(bytes: ByteArray) {
        try {
            val inputStream: InputStream = bytes.inputStream()
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)

            tableLayout.removeAllViews()

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
            Log.e("FoodFragment", "Ошибка отображения файла", e)
        }
    }

    private fun getUserClassFromSharedPreferences(): String {
        val sharedPreferences = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_CLASS", "") ?: ""
    }
}