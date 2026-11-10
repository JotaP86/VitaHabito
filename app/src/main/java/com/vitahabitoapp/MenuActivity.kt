package com.vitahabitoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        userId = intent.getIntExtra("user_id", -1)

        val btnAddHabit = findViewById<Button>(R.id.btnAddHabit)
        val btnViewHabits = findViewById<Button>(R.id.btnViewHabits)

        btnAddHabit.setOnClickListener {
            val intent = Intent(this, HabitFormActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }

        btnViewHabits.setOnClickListener {
            val intent = Intent(this, HabitListActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }
    }
}
