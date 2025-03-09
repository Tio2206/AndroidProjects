package com.example.multiplelayout

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.util.Calendar

class EditBookAct : AppCompatActivity() {
    private lateinit var bookImage: ImageView
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedImageUri: Uri? = null
    private var bookId: Int = -1

    // ActivityResultLauncher for picking an image
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                Glide.with(this).load(it).into(bookImage)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        bookId = intent.getIntExtra("BOOK_ID", -1)
        databaseHelper = DatabaseHelper(this)
        val book = databaseHelper.getBookById(bookId)

        // Initialize UI elements
        val nameEditText = findViewById<EditText>(R.id.editBookName)
        val nicknameEditText = findViewById<EditText>(R.id.editBookNickname)
        val emailEditText = findViewById<EditText>(R.id.editBookEmail)
        val addressEditText = findViewById<EditText>(R.id.editBookAddress)
        val phoneEditText = findViewById<EditText>(R.id.editBookPhone)
        val birthdateEditText = findViewById<EditText>(R.id.editBookBirthdate)
        bookImage = findViewById(R.id.editBookImage)
        val changeImageButton = findViewById<Button>(R.id.btnChangeImage)
        val saveButton = findViewById<Button>(R.id.btnSave)

        // Set existing values
        nameEditText.setText(book?.name ?: "")
        nicknameEditText.setText(book?.nickname ?: "")
        emailEditText.setText(book?.email ?: "")
        addressEditText.setText(book?.address ?: "")
        phoneEditText.setText(book?.phone ?: "")
        birthdateEditText.setText(book?.birthdate ?: "")

        book?.imagePath?.let {
            Glide.with(this).load(it).into(bookImage)
        }

        // Disable direct text input for birthdate field
        birthdateEditText.isFocusable = false
        birthdateEditText.isClickable = true

        // Open DatePickerDialog when birthdate field is clicked
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

        // Handle image selection when "Change Image" button is clicked
        changeImageButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Save updated book information
        saveButton.setOnClickListener {
            val updatedBook = Book(
                bookId,
                nameEditText.text.toString(),
                nicknameEditText.text.toString(),
                emailEditText.text.toString(),
                addressEditText.text.toString(),
                phoneEditText.text.toString(),
                birthdateEditText.text.toString(),
                selectedImageUri?.toString() ?: book?.imagePath // Save new image URI if changed
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
