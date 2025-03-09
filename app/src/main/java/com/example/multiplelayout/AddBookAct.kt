package com.example.multiplelayout

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.multiplelayout.databinding.ActivityAddBookBinding
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class AddBookAct : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var imagePath: String? = null // To store the selected image path

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        binding.btnSelectPhoto.setOnClickListener { openImagePicker() }
        binding.etTglLahir.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { saveBookToDatabase() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun openImagePicker() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val chooser = Intent.createChooser(pickIntent, "Pilih Foto")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePhotoIntent))
        imagePickerLauncher.launch(chooser)
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                if (data.data != null) {
                    // Image picked from gallery
                    val uri: Uri = data.data!!
                    binding.imgPhoto.setImageURI(uri)
                    imagePath = uri.toString()
                } else if (data.extras != null) {
                    // Image captured from camera
                    val bitmap: Bitmap = data.extras!!.get("data") as Bitmap
                    binding.imgPhoto.setImageBitmap(bitmap)
                    imagePath = saveImageToInternalStorage(bitmap)
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
            val selectedDate = "$day/${month + 1}/$year"
            binding.etTglLahir.setText(selectedDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.show()
    }

    private fun saveBookToDatabase() {
        val name = binding.etNama.text.toString()
        val nickname = binding.etNamaPanggilan.text.toString()
        val email = binding.etEmail.text.toString()
        val address = binding.etAlamat.text.toString()
        val phone = binding.etNoHp.text.toString()
        val birthdate = binding.etTglLahir.text.toString()

        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
            Toast.makeText(this, "Nama, Email, dan No HP harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val book = Book(
            name = name,
            nickname = nickname,
            email = email,
            address = address,
            phone = phone,
            birthdate = birthdate,
            imagePath = imagePath ?: ""
        )

        databaseHelper.insertBook(book)
        Toast.makeText(this, "Buku berhasil disimpan!", Toast.LENGTH_SHORT).show()

        // Use Intent to navigate back to BooksAct
        val intent = Intent(this, BooksAct::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Ensures BooksAct restarts
        startActivity(intent)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, filename)

        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        return file.absolutePath
    }
}
