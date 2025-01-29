package com.example.multiplelayout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var exit : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        exit = findViewById(R.id.button)
        exit.setOnClickListener(this)
        // Inisialisasi view setelah setContentView
        val codeIcon = findViewById<ImageView>(R.id.imageView) // ID dari ImageView untuk ikon "Code"
        val calculatorButton = findViewById<LinearLayout>(R.id.calculator_button)

        calculatorButton.setOnClickListener {
            val intent = Intent(this, CalculatorActivity::class.java)
            startActivity(intent)
        }

        // Tambahkan onClickListener ke codeIcon
        codeIcon.setOnClickListener {
            AlertDialog.Builder(this) // Menggunakan AlertDialog dari androidx
                .setTitle("Code Icon")
                .setMessage("Lorem Ipsum is simply dummy text of the printing and typesetting industry.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun onClick(v: View?) {
        finishAffinity()  // Closes all activities and terminates the app
    }
}
