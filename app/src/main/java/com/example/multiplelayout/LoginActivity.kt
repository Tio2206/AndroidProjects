package com.example.multiplelayout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nama = findViewById<EditText>(R.id.editTextText)
        val pass = findViewById<EditText>(R.id.editTextTextPassword)

        val buttonClick = findViewById<Button>(R.id.button2)

        buttonClick.setOnClickListener {
            if (nama.text.toString()=="user" && pass.text.toString()=="123") {
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show()

                startActivity(intent)
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }

        }
    }
}