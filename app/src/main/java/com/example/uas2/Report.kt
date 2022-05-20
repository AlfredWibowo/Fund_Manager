package com.example.uas2

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class Report : AppCompatActivity() {

    private lateinit var _tvDate : TextView

    lateinit var db : FirebaseFirestore

    private lateinit var _rvTransactionbyDate : RecyclerView

    private var arrTransaction = arrayListOf<TransactionCls>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        _tvDate = findViewById(R.id.tvDate)

        //calendar
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        //declare curr date
        _tvDate.text = day.toString() + "/" + month.toString() + "/" + year.toString()

        //----------------------------------------------------------

        _rvTransactionbyDate = findViewById(R.id.rvTransactionbyDate)

        db = FirebaseFirestore.getInstance()

        //load awal curr date
        LoadData(db, _tvDate.text.toString())

        //date picker
        _tvDate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, mYear, mMonth, mDay ->
                _tvDate.setText(mDay.toString() + "/" + mMonth.toString() + "/" + mYear.toString())
                LoadData(db, _tvDate.text.toString())
            }, year, month, day)
            dpd.show()

        }

        //----------------------nav bot------------------------------

        val _ibBalance = findViewById<ImageButton>(R.id.ibBalance3)
        _ibBalance.setOnClickListener {
            val balanceIntent = Intent(this@Report, MainActivity::class.java)
            startActivity(balanceIntent)
        }

        val _ibCategory = findViewById<ImageButton>(R.id.ibCategory3)
        _ibCategory.setOnClickListener {
            val categoryIntent = Intent(this@Report, Category::class.java)
            startActivity(categoryIntent)
        }

        val _ibReport = findViewById<ImageButton>(R.id.ibReport3)
        _ibReport.setOnClickListener {
            //nothing
        }
    }

    private fun LoadData(db : FirebaseFirestore, date : String) {
        db.collection("tbTransaction").get()
            .addOnSuccessListener { result ->
                arrTransaction.clear()

                for (document in result) {
                    val dataBaru = TransactionCls(
                        document.data.get("idTransaksi").toString(),
                        document.data.get("tanggal").toString(),
                        document.data.get("kategori").toString(),
                        document.data.get("deskripsi").toString(),
                        document.data.get("amount").toString(),
                        document.data.get("tipe").toString()
                    )
                    arrTransaction.add(dataBaru)
                }

                //filter data by tanggal
                val arrTransactionbyDate = ArrayList<TransactionCls>()
                arrTransaction.forEach {
                    if (it.Tanggal == _tvDate.text.toString()) {
                        arrTransactionbyDate.add(it)
                    }
                }
                DisplayData(arrTransactionbyDate)
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    private fun DisplayData(arr : ArrayList<TransactionCls>) {
        val transactionAdapter = adapterTransaction(arr)
        _rvTransactionbyDate.adapter = transactionAdapter
        _rvTransactionbyDate.layoutManager = LinearLayoutManager(this)

        transactionAdapter.setOnItemClickCallback(object : adapterTransaction.OnItemClickCallback {
            override fun onDeleteClicked(data: TransactionCls) {
                DeleteData(db, data)
            }

        })
    }

    private fun DeleteData(db : FirebaseFirestore, data : TransactionCls) {
        AlertDialog.Builder(this@Report)
            .setTitle("HAPUS TRANSAKSI")
            .setMessage("Apakah benar data dengan kategori " +  data.Kategori + " dan deskripsi " + data.Deskripsi + " akan dihapus?")
            .setPositiveButton(
                "HAPUS",
                DialogInterface.OnClickListener { dialogInterface, i ->

                    db.collection("tbTransaction").document(data.IdTransaksi)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@Report,
                                "Delete Successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            LoadData(db, _tvDate.text.toString())

                            Log.d("Firebase", "success")
                        }
                        .addOnFailureListener {
                            Log.d("Firebase", it.message.toString())
                        }
                }
            )
            .setNegativeButton(
                "BATAL",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    Toast.makeText(
                        this@Report,
                        "Data Batal Dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ).show()


    }
}