package com.vitahabitoapp

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyCheckActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView
    private var userId = -1
    private val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_check)

        db = DatabaseHelper(this)
        userId = intent.getIntExtra("user_id", -1)
        listView = findViewById(R.id.lvDailyCheck)

        loadData()
    }

    private fun loadData() {
        val habits = db.getHabitsByUser(userId)
        val logs = habits.associateWith {
            db.getLogsByHabit(it.id).find { log -> log.date == today }?.status == 1
        }

        val adapter = DailyCheckAdapter(this, habits, logs) { habit, checked ->
            db.insertOrUpdateLog(habit.id, today, if (checked) 1 else 0, null)
        }

        listView.adapter = adapter
    }
}
