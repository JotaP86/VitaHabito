package com.vitahabitoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class DailyCheckAdapter(
    private val context: Context,
    private val habits: List<Habit>,
    private val logs: Map<Habit, Boolean>,
    private val onCheckChanged: (Habit, Boolean) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = habits.size
    override fun getItem(i: Int): Habit = habits[i]
    override fun getItemId(i: Int): Long = habits[i].id.toLong()

    override fun getView(i: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_daily_check, parent, false)

        val habit = habits[i]
        val txtName = view.findViewById<TextView>(R.id.txtHabitName)
        val check = view.findViewById<CheckBox>(R.id.cbDone)

        txtName.text = habit.name
        check.isChecked = logs[habit] == true

        check.setOnCheckedChangeListener { _, isChecked ->
            onCheckChanged(habit, isChecked)
        }

        return view
    }
}
