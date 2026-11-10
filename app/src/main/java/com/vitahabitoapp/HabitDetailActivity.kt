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
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_detail)

        db = DatabaseHelper(this)

        habitId = intent.getIntExtra("HABIT_ID", -1)
        userId = intent.getIntExtra("userId", -1)

        val txtName = findViewById<TextView>(R.id.txtDetailName)
        val txtDesc = findViewById<TextView>(R.id.txtDetailDescription)
        val btnEdit = findViewById<Button>(R.id.btnEditHabit)
        val btnDelete = findViewById<Button>(R.id.btnDeleteHabit)

        val habit = db.getHabitById(habitId)

        if (habit != null) {
            txtName.text = habit.name
            txtDesc.text = habit.description
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, HabitEditActivity::class.java)
            intent.putExtra("HABIT_ID", habitId)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            val rows = db.deleteHabit(habitId)

            if (rows > 0) {
                Toast.makeText(this, "Hábito eliminado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
