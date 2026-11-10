package com.vitahabitoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HabitEditActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var habitId: Int = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_edit)

        db = DatabaseHelper(this)

        habitId = intent.getIntExtra("HABIT_ID", -1)
        userId = intent.getIntExtra("userId", -1)

        val txtName = findViewById<EditText>(R.id.editHabitName)
        val txtDesc = findViewById<EditText>(R.id.editHabitDescription)
        val btnSave = findViewById<Button>(R.id.btnUpdateHabit)

        val habit = db.getHabitById(habitId)
        if (habit != null) {
            txtName.setText(habit.name)
            txtDesc.setText(habit.description)
        }

        btnSave.setOnClickListener {
            val newName = txtName.text.toString().trim()
            val newDesc = txtDesc.text.toString().trim()

            if (newName.isEmpty() || newDesc.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rows = db.updateHabit(habitId, newName, newDesc)

            if (rows > 0) {
                Toast.makeText(this, "Hábito actualizado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
