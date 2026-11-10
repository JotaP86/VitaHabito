package com.vitahabitoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HabitDetailActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var habitId: Int = -1

    private lateinit var txtName: TextView
    private lateinit var txtDescription: TextView
    private lateinit var txtCategory: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_detail)

        db = DatabaseHelper(this)
        habitId = intent.getIntExtra("HABIT_ID", -1)

        txtName = findViewById(R.id.txtDetailName)
        txtDescription = findViewById(R.id.txtDetailDescription)
        // txtDetailCategory puede no existir en tu layout; si no existe, crea uno o comenta la línea
        val catExists = resources.getIdentifier("txtDetailCategory", "id", packageName) != 0
        if (catExists) {
            txtCategory = findViewById(R.id.txtDetailCategory)
        }

        btnEdit = findViewById(R.id.btnEditHabit)
        btnDelete = findViewById(R.id.btnDeleteHabit)

        loadHabit()

        btnEdit.setOnClickListener {
            try {
                if (habitId == -1) {
                    Toast.makeText(this, "Hábito inválido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val i = Intent(this, HabitEditActivity::class.java)
                i.putExtra("HABIT_ID", habitId)
                startActivity(i)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir editor: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

        btnDelete.setOnClickListener {
            if (habitId != -1) {
                db.deleteHabit(habitId)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadHabit()
    }

    private fun loadHabit() {
        if (habitId == -1) return

        val habit = db.getHabitById(habitId) ?: return

        txtName.text = habit.name
        txtDescription.text = habit.description

        if (::txtCategory.isInitialized) {
            val cat = db.getCategoryById(habit.categoryId)
            txtCategory.text = cat?.name ?: "Sin categoría"
        }
    }
}
