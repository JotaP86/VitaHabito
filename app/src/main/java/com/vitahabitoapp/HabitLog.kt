package com.vitahabitoapp

data class HabitLog(
    val id: Int,
    val habitId: Int,
    val date: String,
    val status: Int,
    val notes: String?
)
