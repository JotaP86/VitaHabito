package com.vitahabitoapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "vitahabito.db"
        const val DB_VERSION = 5

        // USERS
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"

        // CATEGORIES
        const val TABLE_CATEGORIES = "categories"
        const val COL_CAT_ID = "id"
        const val COL_CAT_NAME = "name"

        // HABITS
        const val TABLE_HABITS = "habits"
        const val COL_HABIT_ID = "id"
        const val COL_HABIT_NAME = "name"
        const val COL_HABIT_DESC = "description"
        const val COL_HABIT_USER_ID = "user_id"
        const val COL_HABIT_CATEGORY_ID = "category_id"

        // LOGS
        const val TABLE_LOGS = "habit_logs"
        const val COL_LOG_ID = "id"
        const val COL_LOG_HABIT_ID = "habit_id"
        const val COL_LOG_DATE = "date"
        const val COL_LOG_STATUS = "status"
        const val COL_LOG_NOTES = "notes"
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsers = """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE,
                $COL_PASSWORD TEXT
            );
        """.trimIndent()

        val createCategories = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COL_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CAT_NAME TEXT UNIQUE
            );
        """.trimIndent()

        val createHabits = """
            CREATE TABLE $TABLE_HABITS (
                $COL_HABIT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_HABIT_NAME TEXT,
                $COL_HABIT_DESC TEXT,
                $COL_HABIT_USER_ID INTEGER,
                $COL_HABIT_CATEGORY_ID INTEGER,
                FOREIGN KEY($COL_HABIT_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
                FOREIGN KEY($COL_HABIT_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COL_CAT_ID)
            );
        """.trimIndent()

        val createLogs = """
            CREATE TABLE $TABLE_LOGS (
                $COL_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_LOG_HABIT_ID INTEGER,
                $COL_LOG_DATE TEXT,
                $COL_LOG_STATUS INTEGER,
                $COL_LOG_NOTES TEXT,
                UNIQUE($COL_LOG_HABIT_ID, $COL_LOG_DATE),
                FOREIGN KEY($COL_LOG_HABIT_ID) REFERENCES $TABLE_HABITS($COL_HABIT_ID)
            );
        """.trimIndent()

        db.execSQL(createUsers)
        db.execSQL(createCategories)
        db.execSQL(createHabits)
        db.execSQL(createLogs)

        // Insert default categories
        db.execSQL("INSERT INTO $TABLE_CATEGORIES ($COL_CAT_NAME) VALUES ('Salud')")
        db.execSQL("INSERT INTO $TABLE_CATEGORIES ($COL_CAT_NAME) VALUES ('Productividad')")
        db.execSQL("INSERT INTO $TABLE_CATEGORIES ($COL_CAT_NAME) VALUES ('Personal')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HABITS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // -------------------
    // USERS
    // -------------------
    fun registerUser(username: String, password: String): Long {
        val db = writableDatabase
        return try {
            val v = ContentValues().apply {
                put(COL_USERNAME, username)
                put(COL_PASSWORD, password)
            }
            db.insert(TABLE_USERS, null, v)
        } finally {
            db.close()
        }
    }

    fun loginUser(username: String, password: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_USER_ID FROM $TABLE_USERS WHERE $COL_USERNAME=? AND $COL_PASSWORD=?",
            arrayOf(username, password)
        )
        val res = cursor.use { if (it.moveToFirst()) it.getInt(0) else -1 }
        db.close()
        return res
    }

    // -------------------
    // CATEGORIES
    // -------------------
    fun insertCategory(name: String): Long {
        val db = writableDatabase
        return try {
            val v = ContentValues().apply { put(COL_CAT_NAME, name) }
            db.insert(TABLE_CATEGORIES, null, v)
        } finally {
            db.close()
        }
    }

    fun getAllCategories(): List<Category> {
        val list = mutableListOf<Category>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES ORDER BY $COL_CAT_NAME ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Category(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_CAT_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COL_CAT_NAME))
                    )
                )
            }
        }
        db.close()
        return list
    }

    fun getCategoryById(catId: Int): Category? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES WHERE $COL_CAT_ID=?", arrayOf(catId.toString()))
        val res = cursor.use {
            if (it.moveToFirst()) {
                Category(
                    it.getInt(it.getColumnIndexOrThrow(COL_CAT_ID)),
                    it.getString(it.getColumnIndexOrThrow(COL_CAT_NAME))
                )
            } else null
        }
        db.close()
        return res
    }

    // -------------------
    // HABITS
    // -------------------
    fun insertHabit(name: String, description: String, categoryId: Int, userId: Int): Long {
        val db = writableDatabase
        return try {
            val v = ContentValues().apply {
                put(COL_HABIT_NAME, name)
                put(COL_HABIT_DESC, description)
                put(COL_HABIT_CATEGORY_ID, categoryId)
                put(COL_HABIT_USER_ID, userId)
            }
            db.insert(TABLE_HABITS, null, v)
        } finally {
            db.close()
        }
    }

    fun getHabitsByUser(userId: Int): List<Habit> {
        val list = mutableListOf<Habit>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_HABITS WHERE $COL_HABIT_USER_ID=? ORDER BY $COL_HABIT_ID ASC",
            arrayOf(userId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Habit(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_HABIT_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COL_HABIT_NAME)),
                        description = it.getString(it.getColumnIndexOrThrow(COL_HABIT_DESC)),
                        categoryId = it.getInt(it.getColumnIndexOrThrow(COL_HABIT_CATEGORY_ID))
                    )
                )
            }
        }
        db.close()
        return list
    }

    fun getHabitById(habitId: Int): Habit? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_HABITS WHERE $COL_HABIT_ID=?", arrayOf(habitId.toString()))
        val res = cursor.use {
            if (it.moveToFirst()) {
                Habit(
                    id = it.getInt(it.getColumnIndexOrThrow(COL_HABIT_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(COL_HABIT_NAME)),
                    description = it.getString(it.getColumnIndexOrThrow(COL_HABIT_DESC)),
                    categoryId = it.getInt(it.getColumnIndexOrThrow(COL_HABIT_CATEGORY_ID))
                )
            } else null
        }
        db.close()
        return res
    }

    fun updateHabit(habitId: Int, name: String, description: String, categoryId: Int): Int {
        val db = writableDatabase
        return try {
            val v = ContentValues().apply {
                put(COL_HABIT_NAME, name)
                put(COL_HABIT_DESC, description)
                put(COL_HABIT_CATEGORY_ID, categoryId)
            }
            db.update(TABLE_HABITS, v, "$COL_HABIT_ID=?", arrayOf(habitId.toString()))
        } finally {
            db.close()
        }
    }

    fun deleteHabit(id: Int) {
        val db = writableDatabase
        try {
            db.delete(TABLE_HABITS, "$COL_HABIT_ID=?", arrayOf(id.toString()))
        } finally {
            db.close()
        }
    }

    // -------------------
    // LOGS
    // -------------------
    fun insertOrUpdateLog(habitId: Int, date: String, status: Int, notes: String?): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COL_LOG_HABIT_ID, habitId)
                put(COL_LOG_DATE, date)
                put(COL_LOG_STATUS, status)
                put(COL_LOG_NOTES, notes)
            }
            db.insertWithOnConflict(TABLE_LOGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        } finally {
            db.close()
        }
    }

    fun getLogsByHabit(habitId: Int): List<HabitLog> {
        val list = mutableListOf<HabitLog>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_LOGS WHERE $COL_LOG_HABIT_ID=? ORDER BY $COL_LOG_DATE ASC",
            arrayOf(habitId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    HabitLog(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_LOG_ID)),
                        habitId = it.getInt(it.getColumnIndexOrThrow(COL_LOG_HABIT_ID)),
                        date = it.getString(it.getColumnIndexOrThrow(COL_LOG_DATE)),
                        status = it.getInt(it.getColumnIndexOrThrow(COL_LOG_STATUS)),
                        notes = it.getString(it.getColumnIndexOrThrow(COL_LOG_NOTES))
                    )
                )
            }
        }
        db.close()
        return list
    }
}
