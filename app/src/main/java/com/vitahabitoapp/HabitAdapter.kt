package com.vitahabitoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class HabitAdapter(private val context: Context, private val habits: List<Habit>) : BaseAdapter() {

    private val db = DatabaseHelper(context)

    override fun getCount(): Int = habits.size

    override fun getItem(position: Int): Any = habits[position]

    override fun getItemId(position: Int): Long = habits[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false)

        val habitName = view.findViewById<TextView>(R.id.txtHabitName)
        val habitDesc = view.findViewById<TextView>(R.id.txtHabitDescription)
        val habitCategory = view.findViewById<TextView>(R.id.txtHabitCategory)

        val habit = habits[position]

        val category = db.getCategoryById(habit.categoryId)

        habitName.text = habit.name
        habitDesc.text = habit.description
        habitCategory.text = "Categoría: ${category?.name ?: "Sin categoría"}"

        return view
    }
}
