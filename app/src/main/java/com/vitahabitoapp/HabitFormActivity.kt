package com.vitahabitoapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HabitFormActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var spinnerCat: Spinner
    private lateinit var btnSave: Button

    private var categoryList = listOf<Category>()
    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_form)

        db = DatabaseHelper(this)
        userId = intent.getIntExtra("user_id", -1)

        etName = findViewById(R.id.etHabitName)
        etDesc = findViewById(R.id.etHabitDesc)
        spinnerCat = findViewById(R.id.spinnerCategory)
        btnSave = findViewById(R.id.btnSaveHabit)

        loadCategories()

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val position = spinnerCat.selectedItemPosition
            val categoryId = categoryList[position].id

            db.insertHabit(name, desc, categoryId, userId)

            finish()
        }
    }

    private fun loadCategories() {
        categoryList = db.getAllCategories()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryList.map { it.name }
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCat.adapter = adapter
    }
}
