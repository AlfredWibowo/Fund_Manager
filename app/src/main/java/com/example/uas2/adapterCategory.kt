package com.example.uas2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapterCategory (
    private val listCategory : ArrayList<CategoryCls>
) : RecyclerView.Adapter<adapterCategory.ListViewHolder>() {
    private lateinit var onItemClickCallback : OnItemClickCallback

    interface OnItemClickCallback {
        fun onDeleteClicked(data : CategoryCls)
    }

    fun setOnItemClickCallback (onItemClickCallback : OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        var _category : TextView = itemView.findViewById(R.id.category)
        var _ibDeleteCategory : ImageButton = itemView.findViewById(R.id.ibDeleteCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemcategory, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val category = listCategory[position]

        holder._category.text = category.Nama

        holder._ibDeleteCategory.setOnClickListener {
            onItemClickCallback.onDeleteClicked(listCategory[position])
        }
    }

    override fun getItemCount(): Int {
        return listCategory.size
    }


}