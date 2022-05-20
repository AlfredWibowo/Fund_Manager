package com.example.uas2

import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Category : AppCompatActivity() {

    lateinit var db : FirebaseFirestore

    private var arrCategory = arrayListOf<CategoryCls>()

    private lateinit var _rvCategory : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        _rvCategory = findViewById(R.id.rvCategory)

        db = FirebaseFirestore.getInstance()

        //Load awal
        LoadData(db, "Expend")

        val _radioGroup = findViewById<RadioGroup>(R.id.RadioGroup3)
        _radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val _radioButton = findViewById<RadioButton>(i)
            if (_radioButton != null) {
                LoadData(db, _radioButton.text.toString())
            }
        }

        val _ibAddCategory = findViewById<ImageButton>(R.id.ibAddCategory)
        _ibAddCategory.setOnClickListener {
            val formCategoryIntent = Intent(this@Category, FormCategory::class.java)
            startActivity(formCategoryIntent)
        }

        //----------------------nav bot------------------------------

        val _ibBalance = findViewById<ImageButton>(R.id.ibBalance2)
        _ibBalance.setOnClickListener {
            val balanceIntent = Intent(this@Category, MainActivity::class.java)
            startActivity(balanceIntent)
        }

        val _ibCategory = findViewById<ImageButton>(R.id.ibCategory2)
        _ibCategory.setOnClickListener {
            //nothing
        }

        val _ibReport = findViewById<ImageButton>(R.id.ibReport2)
        _ibReport.setOnClickListener {
            val reportIntent = Intent(this@Category, Report::class.java)
            startActivity(reportIntent)
        }
    }

    private fun LoadData(db : FirebaseFirestore, Checked : String) {
        db.collection("tbCategory").get()
            .addOnSuccessListener { result ->
                arrCategory.clear()

                for (document in result) {
                    val dataBaru = CategoryCls(
                        document.data.get("nama").toString(),
                        document.data.get("tipe").toString()
                    )
                    arrCategory.add(dataBaru)
                }

                //bagi data ke expend dan income
                val arrCategoryExpend = arrayListOf<CategoryCls>()
                val arrCategoryIncome = arrayListOf<CategoryCls>()
                arrCategory.forEach { it ->
                    when (it.Tipe) {
                        "0" -> arrCategoryIncome.add(it)
                        "1" -> arrCategoryExpend.add(it)
                    }
                }

                //cek mana yang mau didisplay
                var arr = ArrayList<CategoryCls>()
                when(Checked) {
                    "Expend" -> arr.addAll(arrCategoryExpend)
                    "Income" -> arr.addAll(arrCategoryIncome)
                }
                DisplayData(arr)

            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    private fun DisplayData(arr : ArrayList<CategoryCls>) {
        val categoryAdapter = adapterCategory(arr)
        _rvCategory.adapter = categoryAdapter
        _rvCategory.layoutManager = LinearLayoutManager(this)

        categoryAdapter.setOnItemClickCallback(object : adapterCategory.OnItemClickCallback {
            override fun onDeleteClicked(data: CategoryCls) {
                DeleteData(db, data)
            }
        })
    }

    private fun DeleteData(db: FirebaseFirestore, data : CategoryCls) {
        AlertDialog.Builder(this@Category)
            .setTitle("HAPUS KATEGORI")
            .setMessage("Apakah benar kategori " +  data.Nama + " akan dihapus?")
            .setPositiveButton(
                "HAPUS",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    db.collection("tbCategory").document(data.Nama)
                        .delete()
                        .addOnSuccessListener {

                            Toast.makeText(
                                this@Category,
                                "Delete Successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            val rdbtnExpend = findViewById<RadioButton>(R.id.rbExpend3)
                            when (rdbtnExpend.isChecked) {
                                true -> LoadData(db, "Expend")
                                false -> LoadData(db, "Income")
                            }

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
                        this@Category,
                        "Kategori Batal Dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ).show()

    }

}