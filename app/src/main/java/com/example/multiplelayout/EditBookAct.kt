package com.example.multiplelayout

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.util.Calendar

class EditBookAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        val bookId = intent.getIntExtra("BOOK_ID", -1)
        val databaseHelper = DatabaseHelper(this)
        val book = databaseHelper.getBookById(bookId)

        val nameEditText = findViewById<EditText>(R.id.editBookName)
        val nicknameEditText = findViewById<EditText>(R.id.editBookNickname)
        val emailEditText = findViewById<EditText>(R.id.editBookEmail)
        val addressEditText = findViewById<EditText>(R.id.editBookAddress)
        val phoneEditText = findViewById<EditText>(R.id.editBookPhone)
        val birthdateEditText = findViewById<EditText>(R.id.editBookBirthdate)
        val bookImage = findViewById<ImageView>(R.id.editBookImage)
        val saveButton = findViewById<Button>(R.id.btnSave)

        // Set existing values from the database
        nameEditText.setText(book?.name ?: "")
        nicknameEditText.setText(book?.nickname ?: "")
        emailEditText.setText(book?.email ?: "")
        addressEditText.setText(book?.address ?: "")
        phoneEditText.setText(book?.phone ?: "")
        birthdateEditText.setText(book?.birthdate ?: "")

        book?.imagePath?.let {
            Glide.with(this).load(it).into(bookImage)
        }

        // Disable direct text input for the birthdate field
        birthdateEditText.isFocusable = false
        birthdateEditText.isClickable = true

        // Show DatePickerDialog when clicking birthdate field
        birthdateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                birthdateEditText.setText(selectedDate)
            }, year, month, day).show()
        }

        // Save updated book info
        saveButton.setOnClickListener {
            val updatedBook = Book(
                bookId,
                nameEditText.text.toString(),
                nicknameEditText.text.toString(),
                emailEditText.text.toString(),
                addressEditText.text.toString(),
                phoneEditText.text.toString(),
                birthdateEditText.text.toString(),
                book?.imagePath
            )

            databaseHelper.updateBook(updatedBook)
            Toast.makeText(this, "Buku berhasil diupdate!", Toast.LENGTH_SHORT).show()

            // Navigate back to BooksAct
            val intent = Intent(this, BooksAct::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}
