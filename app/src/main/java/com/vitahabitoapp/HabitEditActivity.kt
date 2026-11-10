package com.vitahabitoapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class HabitEditActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var spinnerCat: Spinner
    private lateinit var btnSave: Button

    private var habitId = -1
    private var categories: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_edit)

        db = DatabaseHelper(this)
        habitId = intent.getIntExtra("HABIT_ID", -1)

        etName = findViewById(R.id.editHabitName)
        etDesc = findViewById(R.id.editHabitDescription)
        spinnerCat = findViewById(R.id.spinnerEditCategory)
        btnSave = findViewById(R.id.btnUpdateHabit)

        loadCategories()
        loadHabit()

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedCategory = categories[spinnerCat.selectedItemPosition]


            db.updateHabit(
                habitId = habitId,
                name = name,
                description = desc,
                categoryId = selectedCategory.id
            )

            Toast.makeText(this, "Hábito actualizado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadCategories() {
        categories = db.getAllCategories()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCat.adapter = adapter
    }

    private fun loadHabit() {
        val habit = db.getHabitById(habitId)
        if (habit != null) {
            etName.setText(habit.name)
            etDesc.setText(habit.description)

            val index = categories.indexOfFirst { it.id == habit.categoryId }
            if (index >= 0) spinnerCat.setSelection(index)
        }
    }
}
