package com.example.uas2

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FormTransaction : AppCompatActivity() {

    lateinit var db : FirebaseFirestore

    private var arrTransaction = arrayListOf<TransactionCls>()

    private var arrCategory = arrayListOf<CategoryCls>()

    private lateinit var _actvCategory : AutoCompleteTextView

    var _idTransaction : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_transaction)

        db = FirebaseFirestore.getInstance()

        val _btnBacktoBalance = findViewById<ImageButton>(R.id.ibBacktoBalance)
        _btnBacktoBalance.setOnClickListener {
            val balanceIntent = Intent(this@FormTransaction, MainActivity::class.java)
            startActivity(balanceIntent)
        }

        //load awal
        LoadData(db)
        LoadCategory(db, "Expend")

        var _tipe = "1"
        val _tvDate = findViewById<TextView>(R.id.tvDateTransaction)
        _actvCategory = findViewById(R.id.actvCategory)
        val _etDescription = findViewById<EditText>(R.id.etDescription)
        val _etAmount = findViewById<EditText>(R.id.etAmount)

        val _radioGroup = findViewById<RadioGroup>(R.id.RadioGroup2)
        _radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val _radioButton = findViewById<RadioButton>(i)
            if (_radioButton != null) {
                when (_radioButton.text.toString()) {
                    "Expend" -> _tipe = "1"
                    "Income" -> _tipe = "0"
                }
                LoadCategory(db, _radioButton.text.toString())
            }
        }

        //calendar
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        //date picker

        _tvDate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, mYear, mMonth, mDay ->
                _tvDate.setText(mDay.toString() + "/" + mMonth.toString() + "/" + mYear.toString())
            }, year, month, day)
            dpd.show()
        }

        val _ibDate = findViewById<ImageButton>(R.id.ibDate)
        _ibDate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, mYear, mMonth, mDay ->
                _tvDate.setText(mDay.toString() + "/" + mMonth.toString() + "/" + mYear.toString())
            }, year, month, day)
            dpd.show()
        }

        val _btnSaveTransaction = findViewById<Button>(R.id.btnSaveTransaction)
        _btnSaveTransaction.setOnClickListener {
            _idTransaction = "0"

            val listId = mutableListOf<String>()

            arrTransaction.forEach { it ->
                listId.add(it.IdTransaksi)
            }

            if (listId.size != 0) {
                _idTransaction = (listId.maxOrNull().toString().toInt() + 1).toString()
                Log.d("idTransaksi", _idTransaction)
            }

            val dataBaru = TransactionCls(
                _idTransaction,
                _tvDate.text.toString(),
                _actvCategory.text.toString(),
                _etDescription.text.toString(),
                _etAmount.text.toString(),
                _tipe
            )

            if (FormChecking(dataBaru.Tanggal, dataBaru.Amount, dataBaru.Kategori)) {
                Toast.makeText(
                    this@FormTransaction,
                    "Save Successful",
                    Toast.LENGTH_SHORT
                ).show()

                SaveData(db, dataBaru)
            }
            else {
                Toast.makeText(
                    this@FormTransaction,
                    "Check Again!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun FormChecking(date : String, amount : String, category : String ) : Boolean {
        if (date != "" && amount != "" && category != "Select...") {
            return true
        }
        return false
    }

    private fun LoadData(db : FirebaseFirestore) {
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
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    private fun LoadCategory(db: FirebaseFirestore, tipe : String) {
        db.collection("tbCategory").get()
            .addOnSuccessListener { result ->
                arrCategory.clear()

                for (document in result) {
                    val dataBaru = CategoryCls(
                        document.data.get("nama").toString(),
                        document.data.get("tipe").toString(),
                    )
                    arrCategory.add(dataBaru)
                }

                //bagi category ke expend dan income
                val arrCategoryExpend = arrayListOf<String>()
                val arrCategoryIncome = arrayListOf<String>()
                arrCategory.forEach { it ->
                    when (it.Tipe) {
                        "0" -> arrCategoryIncome.add(it.Nama)
                        "1" -> arrCategoryExpend.add(it.Nama)
                    }
                }

                //dropdown kategori
                if (tipe == "Expend") {
                    val dropdownAdapter = ArrayAdapter(this, R.layout.itemdropdown, arrCategoryExpend)
                    _actvCategory.setAdapter(dropdownAdapter)
                }
                else if (tipe == "Income") {
                    val dropdownAdapter = ArrayAdapter(this, R.layout.itemdropdown, arrCategoryIncome)
                    _actvCategory.setAdapter(dropdownAdapter)
                }
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }

    }

    private fun SaveData(db: FirebaseFirestore, data : TransactionCls) {
        db.collection("tbTransaction").document(data.IdTransaksi)
            .set(data)
            .addOnSuccessListener {
                val balanceIntent = Intent(this@FormTransaction, MainActivity::class.java)
                startActivity(balanceIntent)
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

}