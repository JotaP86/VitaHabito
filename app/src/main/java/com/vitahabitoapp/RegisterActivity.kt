package com.vitahabitoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = DatabaseHelper(this)

        val txtUser = findViewById<EditText>(R.id.etUsernameRegister)
        val txtPass = findViewById<EditText>(R.id.etPasswordRegister)
        val txtRepeat = findViewById<EditText>(R.id.etRepeatPasswordRegister)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val u = txtUser.text.toString().trim()
            val p = txtPass.text.toString().trim()
            val rp = txtRepeat.text.toString().trim()

            if (u.isEmpty() || p.isEmpty() || rp.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (p != rp) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = db.registerUser(u, p)

            if (result > 0) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Ese usuario ya existe", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
