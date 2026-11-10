package com.vitahabitoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HabitFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_form)

        val txtName = findViewById<EditText>(R.id.editTextName)
        val txtDescription = findViewById<EditText>(R.id.editTextDescription)
        val btnSave = findViewById<Button>(R.id.btnSaveHabit)

        val db = DatabaseHelper(this)

        // -> Recibimos el userId desde la pantalla anterior
        val userId = intent.getIntExtra("userId", -1)

        btnSave.setOnClickListener {
            val name = txtName.text.toString().trim()
            val desc = txtDescription.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = db.insertHabit(name, desc, userId)

            if (result > 0) {
                Toast.makeText(this, "Hábito guardado", Toast.LENGTH_SHORT).show()
                finish()  // vuelve a la lista
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
