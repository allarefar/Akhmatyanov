package com.example.ahmatynov.tabl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ahmatynov.R

class GradesAdapter(private val data: List<List<String>>) :
    RecyclerView.Adapter<GradesAdapter.GradeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return GradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        val rowData = data[position]
        holder.bind(rowData)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class GradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cell: TextView = itemView.findViewById(R.id.cell)

        fun bind(rowData: List<String>) {
            val rowText = rowData.joinToString(separator = "\t")
            cell.text = rowText
        }
    }
}