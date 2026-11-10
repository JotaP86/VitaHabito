package com.vitahabitoapp

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class HabitListActivity : AppCompatActivity() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_list)

        userId = intent.getIntExtra("user_id", -1)

        loadHabits()
    }

    override fun onResume() {
        super.onResume()
        loadHabits()
    }

    private fun loadHabits() {
        val listView = findViewById<ListView>(R.id.listHabits)
        val db = DatabaseHelper(this)

        val habits = db.getHabitsByUser(userId)
        val adapter = HabitAdapter(this, habits)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedHabit = habits[position]

            val intent = Intent(this, HabitDetailActivity::class.java)
            intent.putExtra("HABIT_ID", selectedHabit.id)
            startActivity(intent)
        }
    }
}
