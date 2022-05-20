package com.example.uas2

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var db : FirebaseFirestore

    private var arrTransaction = arrayListOf<TransactionCls>()

    private lateinit var _rvTransaction : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _rvTransaction = findViewById(R.id.rvTransaction)

        db = FirebaseFirestore.getInstance()

        //load awal
        LoadData(db, "All")
        LoadDataAll(db)

        val _radioGroup = findViewById<RadioGroup>(R.id.RadioGroup1)
        _radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val _radioButton = findViewById<RadioButton>(i)
            if (_radioButton != null) {
                LoadData(db, _radioButton.text.toString())
            }
        }

        val _ibAddTransaction = findViewById<ImageButton>(R.id.ibAddTransaction)
        _ibAddTransaction.setOnClickListener {
            val formTransactionIntent = Intent(this@MainActivity, FormTransaction::class.java)
            startActivity(formTransactionIntent)
        }

        //----------------------nav bot------------------------------

        val _ibBalance = findViewById<ImageButton>(R.id.ibBalance)
        _ibBalance.setOnClickListener {
            //nothing
        }

        val _ibCategory = findViewById<ImageButton>(R.id.ibCategory)
        _ibCategory.setOnClickListener {
            val categoryIntent = Intent(this@MainActivity, Category::class.java)
            startActivity(categoryIntent)
        }

        val _ibReport = findViewById<ImageButton>(R.id.ibReport)
        _ibReport.setOnClickListener {
            val reportIntent = Intent(this@MainActivity, Report::class.java)
            startActivity(reportIntent)
        }
    }

    private fun LoadData(db : FirebaseFirestore, Checked : String) {
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

                //bagi data ke expend dan income
                val arrTransactionExpend = arrayListOf<TransactionCls>()
                val arrTransactionIncome = arrayListOf<TransactionCls>()
                arrTransaction.forEach { it ->
                    when (it.Tipe) {
                        "0" -> arrTransactionIncome.add(it)
                        "1" -> arrTransactionExpend.add(it)
                    }
                }

                //cek mana yang mau didisplay
                var arr = ArrayList<TransactionCls>()
                when (Checked) {
                    "All" -> arr.addAll(arrTransaction)
                    "Income" -> arr.addAll(arrTransactionIncome)
                    "Expend" -> arr.addAll(arrTransactionExpend)
                }

                DisplayData(arr)

            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    private fun DisplayData(arr : ArrayList<TransactionCls>) {
        val transactionAdapter = adapterTransaction(arr)
        _rvTransaction.adapter = transactionAdapter
        _rvTransaction.layoutManager = LinearLayoutManager(this)

        transactionAdapter.setOnItemClickCallback(object : adapterTransaction.OnItemClickCallback {
            override fun onDeleteClicked(data: TransactionCls) {
                DeleteData(db, data)
            }

        })
    }

    private fun LoadDataAll(db : FirebaseFirestore) {
        var total = 0
        db.collection("tbTransaction").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val dataBaru = TransactionCls(
                        document.data.get("idTransaksi").toString(),
                        document.data.get("tanggal").toString(),
                        document.data.get("kategori").toString(),
                        document.data.get("deskripsi").toString(),
                        document.data.get("amount").toString(),
                        document.data.get("tipe").toString()
                    )

                    when (dataBaru.Tipe) {
                        "0" -> total = total + dataBaru.Amount.toInt()
                        "1" -> total = total - dataBaru.Amount.toInt()
                    }
                }
                GetBalance(total)
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    private fun GetBalance(total : Int) {
        val _tvBalance = findViewById<TextView>(R.id.tvBalance)
        _tvBalance.text = "Rp. " + total.toString()
    }

    private fun DeleteData(db : FirebaseFirestore, data : TransactionCls) {
        AlertDialog.Builder(this@MainActivity)
            .setTitle("HAPUS TRANSAKSI")
            .setMessage("Apakah benar transaksi dengan kategori " +  data.Kategori + " dan deskripsi " + data.Deskripsi + " akan dihapus?")
            .setPositiveButton(
                "HAPUS",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    db.collection("tbTransaction").document(data.IdTransaksi)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@MainActivity,
                                "Delete Successful",
                                Toast.LENGTH_SHORT
                            ).show()


                            val rdbtnExpend = findViewById<RadioButton>(R.id.rbExpend1)
                            val rdbtnIncome = findViewById<RadioButton>(R.id.rbIncome1)
                            val rdbtnAll = findViewById<RadioButton>(R.id.rbAll1)

                            if (rdbtnExpend.isChecked)
                                LoadData(db, "Expend")
                            else if (rdbtnIncome.isChecked)
                                LoadData(db, "Income")
                            else if (rdbtnAll.isChecked)
                                LoadData(db, "All")

                            LoadDataAll(db)

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
                        this@MainActivity,
                        "Transaksi Batal Dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ).show()


    }
}