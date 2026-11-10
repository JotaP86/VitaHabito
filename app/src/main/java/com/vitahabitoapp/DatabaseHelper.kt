package com.vitahabitoapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper mejorado:
 * - Activa PRAGMA foreign_keys en onConfigure para que las FK funcionen.
 * - Maneja cierres de cursor/BD de forma segura.
 * - Añade getAllHabits() como utilidad.
 * - Métodos claros y documentados.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "vitahabito.db"
        private const val DB_VERSION = 2

        // Tabla usuarios
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"

        // Tabla hábitos
        const val TABLE_HABITS = "habits"
        const val COL_HABIT_ID = "id"
        const val COL_HABIT_NAME = "name"
        const val COL_HABIT_DESC = "description"
        const val COL_HABIT_USER_ID = "user_id"
    }

    // Habilitar foreign keys para que las relaciones funcionen correctamente
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsers = """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE,
                $COL_PASSWORD TEXT
            );
        """.trimIndent()

        val createHabits = """
            CREATE TABLE $TABLE_HABITS (
                $COL_HABIT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_HABIT_NAME TEXT,
                $COL_HABIT_DESC TEXT,
                $COL_HABIT_USER_ID INTEGER,
                FOREIGN KEY($COL_HABIT_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        db.execSQL(createUsers)
        db.execSQL(createHabits)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // En un entorno real deberías migrar datos; aquí simplificamos recreando tablas.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HABITS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // ---------------------------
    // USUARIOS
    // ---------------------------

    /**
     * Registra un usuario nuevo.
     * Devuelve el id insertrado (>0) o -1 si falla (por ejemplo usuario repetido).
     */
    fun registerUser(username: String, password: String): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COL_USERNAME, username)
                put(COL_PASSWORD, password)
            }
            db.insert(TABLE_USERS, null, values)
        } finally {
            db.close()
        }
    }

    /**
     * Intenta hacer login con usuario/contraseña.
     * Devuelve el userId (>0) si existe, o -1 si no coincide.
     */
    fun loginUser(username: String, password: String): Int {
        val db = readableDatabase
        var cursor: Cursor? = null
        return try {
            val query = """
                SELECT $COL_USER_ID FROM $TABLE_USERS
                WHERE $COL_USERNAME = ? AND $COL_PASSWORD = ?
            """.trimIndent()
            cursor = db.rawQuery(query, arrayOf(username, password))
            if (cursor.moveToFirst()) cursor.getInt(0) else -1
        } finally {
            cursor?.close()
            db.close()
        }
    }

    // ---------------------------
    // HÁBITOS (CRUD)
    // ---------------------------

    /**
     * Inserta un hábito asociado a un userId.
     * Devuelve rowId (>0) o -1 si falla.
     */
    fun insertHabit(name: String, description: String, userId: Int): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COL_HABIT_NAME, name)
                put(COL_HABIT_DESC, description)
                put(COL_HABIT_USER_ID, userId)
            }
            db.insert(TABLE_HABITS, null, values)
        } finally {
            db.close()
        }
    }

    /**
     * Obtiene todos los hábitos de un usuario (ordenados por id asc).
     */
    fun getHabitsByUser(userId: Int): List<Habit> {
        val db = readableDatabase
        val habits = mutableListOf<Habit>()
        var cursor: Cursor? = null
        try {
            val query = "SELECT * FROM $TABLE_HABITS WHERE $COL_HABIT_USER_ID = ? ORDER BY $COL_HABIT_ID ASC"
            cursor = db.rawQuery(query, arrayOf(userId.toString()))
            if (cursor.moveToFirst()) {
                do {
                    habits.add(cursorToHabit(cursor))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
            db.close()
        }
        return habits
    }

    /**
     * (Utilidad) Obtiene todos los hábitos de la tabla (sin filtrar).
     * Útil para debugging. En producción puedes no usarla.
     */
    fun getAllHabits(): List<Habit> {
        val db = readableDatabase
        val habits = mutableListOf<Habit>()
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_HABITS ORDER BY $COL_HABIT_ID ASC", null)
            if (cursor.moveToFirst()) {
                do {
                    habits.add(cursorToHabit(cursor))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
            db.close()
        }
        return habits
    }

    /**
     * Obtiene un hábito por su id, o null si no existe.
     */
    fun getHabitById(id: Int): Habit? {
        val db = readableDatabase
        var cursor: Cursor? = null
        return try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_HABITS WHERE $COL_HABIT_ID = ?", arrayOf(id.toString()))
            if (cursor.moveToFirst()) cursorToHabit(cursor) else null
        } finally {
            cursor?.close()
            db.close()
        }
    }

    /**
     * Actualiza nombre y descripción de un hábito (por id).
     * Devuelve número de filas afectadas.
     */
    fun updateHabit(id: Int, name: String, description: String): Int {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COL_HABIT_NAME, name)
                put(COL_HABIT_DESC, description)
            }
            db.update(TABLE_HABITS, values, "$COL_HABIT_ID = ?", arrayOf(id.toString()))
        } finally {
            db.close()
        }
    }

    /**
     * Borra un hábito por id.
     * Devuelve número de filas eliminadas.
     */
    fun deleteHabit(id: Int): Int {
        val db = writableDatabase
        return try {
            db.delete(TABLE_HABITS, "$COL_HABIT_ID = ?", arrayOf(id.toString()))
        } finally {
            db.close()
        }
    }

    // ---------------------------
    // Helpers internos
    // ---------------------------

    /**
     * Convierte la fila actual del cursor en un objeto Habit.
     * Asume que el cursor está posicionado en una fila válida.
     */
    private fun cursorToHabit(cursor: Cursor): Habit {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HABIT_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_HABIT_NAME))
        val desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_HABIT_DESC))
        return Habit(id = id, name = name, description = desc)
    }
}

