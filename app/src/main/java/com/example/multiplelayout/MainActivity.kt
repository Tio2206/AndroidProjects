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
    private lateinit var codeIcon : ImageView
    private lateinit var exit : Button
    private lateinit var notes : LinearLayout
    private lateinit var calculator : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        codeIcon = findViewById(R.id.imageView)
        codeIcon.setOnClickListener(this)
        exit = findViewById(R.id.button)
        exit.setOnClickListener(this)
        calculator = findViewById(R.id.calculator_button)
        calculator.setOnClickListener(this)
        notes = findViewById(R.id.notes_btn)
        notes.setOnClickListener(this)
        // Inisialisasi view setelah setContentView
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            // Tambahkan onClickListener ke codeIcon
            R.id.imageView->{
                AlertDialog.Builder(this) // Menggunakan AlertDialog dari androidx
                    .setTitle("Code Icon")
                    .setMessage("Lorem Ipsum is simply dummy text of the printing and typesetting industry.")
                    .setPositiveButton("OK", null)
                    .show()
            }
            R.id.button->{
                finishAffinity()  // Closes all activities and terminates the app
            }
            R.id.calculator_button->{
                val intent = Intent(this, CalculatorActivity::class.java)
                startActivity(intent)
            }
            R.id.notes_btn->{
                val intent = Intent(this, NotesActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
