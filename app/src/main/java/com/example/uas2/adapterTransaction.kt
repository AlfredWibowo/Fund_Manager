package com.example.uas2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class adapterTransaction (
    private val listTransaction : ArrayList<TransactionCls>
) : RecyclerView.Adapter<adapterTransaction.ListViewHolder>() {
    private lateinit var onItemClickCallback : OnItemClickCallback

    interface OnItemClickCallback {
        fun onDeleteClicked(data : TransactionCls)

    }

    fun setOnItemClickCallback (onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        var _tanggal : TextView = itemView.findViewById(R.id.tanggal)
        var _kategori : TextView = itemView.findViewById(R.id.kategori)
        var _deskripsi : TextView = itemView.findViewById(R.id.deskripsi)
        var _amount : TextView = itemView.findViewById(R.id.amount)
        var _btnDeleteTransaction : TextView = itemView.findViewById(R.id.btnDeleteTransaction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemtransaction, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val transaction = listTransaction[position]

        holder._tanggal.text = transaction.Tanggal
        holder._kategori.text = transaction.Kategori
        holder._deskripsi.text = transaction.Deskripsi
        holder._amount.text = "Rp. "+ transaction.Amount

        when (transaction.Tipe) {
            //expend
            "1" -> holder._amount.setTextColor(Color.RED)
            //income
            "0" -> holder._amount.setTextColor(Color.GREEN)
        }

        holder._btnDeleteTransaction.setOnClickListener {
            onItemClickCallback.onDeleteClicked(listTransaction[position])
        }

    }

    override fun getItemCount(): Int {
        return listTransaction.size
    }

}