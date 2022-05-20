package com.example.uas2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FormCategory : AppCompatActivity() {

    lateinit var db : FirebaseFirestore

    private var arrCategory = arrayListOf<CategoryCls>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_category)

        db = FirebaseFirestore.getInstance()

        //load data
        LoadData(db)

        val _ibBacktoCategory = findViewById<ImageButton>(R.id.ibBacktoCategory)
        _ibBacktoCategory.setOnClickListener {
            val categoryIntent = Intent(this@FormCategory, Category::class.java)
            startActivity(categoryIntent)
        }

        val _etCategoryName = findViewById<EditText>(R.id.etCategoryName)
        var _tipe = "1"

        val _radioGroup = findViewById<RadioGroup>(R.id.RadioGroup4)
        _radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val _radioButton = findViewById<RadioButton>(i)
            if (_radioButton != null) {
                when (_radioButton.text.toString()) {
                    "Expend" -> _tipe = "1"
                    "Income" -> _tipe = "0"
                }
            }
        }

        val _btnSaveCategory = findViewById<Button>(R.id.btnSaveCategory)
        _btnSaveCategory.setOnClickListener {
            val dataBaru = CategoryCls(_etCategoryName.text.toString(), _tipe)

            if (FormChecking(dataBaru.Nama)) {
                Toast.makeText(
                    this@FormCategory,
                    "Save Successful",
                    Toast.LENGTH_SHORT
                ).show()

                SaveCategory(db, dataBaru)
            }
            else {
                Toast.makeText(
                    this@FormCategory,
                    "Check Again!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun FormChecking(categoryName : String) : Boolean {
        if (categoryName != "") {
            return true
        }
        return false
    }

    private fun SaveCategory(db : FirebaseFirestore, newCtaegory : CategoryCls) {
        db.collection("tbCategory").document(newCtaegory.Nama)
            .set(newCtaegory)
            .addOnSuccessListener {
                val categoryIntent = Intent(this@FormCategory, Category::class.java)
                startActivity(categoryIntent)
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    private fun LoadData(db : FirebaseFirestore) {
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
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }
}