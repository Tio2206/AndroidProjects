package com.example.multiplelayout

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AboutBookAct : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var dbHelper: DatabaseHelper
    private var imageFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_book)

        profileImage = findViewById(R.id.profileImage)
        dbHelper = DatabaseHelper(this)

        val savedImagePath = dbHelper.getProfileImage()
        if (!savedImagePath.isNullOrEmpty()) {
            val file = File(savedImagePath)
            if (file.exists()) {
                profileImage.setImageBitmap(BitmapFactory.decodeFile(savedImagePath))
            }
        }

        findViewById<Button>(R.id.buttonBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        profileImage.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Pick from Gallery", "Take a Photo")

        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> selectImageFromGallery()
                    1 -> captureImageFromCamera()
                }
            }
            .show()
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun captureImageFromCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        } else {
            startCameraIntent()
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            startCameraIntent()
        } else {
            Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCameraIntent() {
        val imageFile = File(filesDir, "profile_camera.jpg")
        imageFilePath = imageFile.absolutePath

        val imageUri: Uri = FileProvider.getUriForFile(this, "$packageName.provider", imageFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                val imagePath = saveImageToInternalStorage(imageUri)
                if (imagePath != null) {
                    profileImage.setImageBitmap(BitmapFactory.decodeFile(imagePath))
                    dbHelper.saveProfileImage(imagePath)
                }
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && imageFilePath != null) {
            val bitmap = BitmapFactory.decodeFile(imageFilePath)
            profileImage.setImageBitmap(bitmap)
            dbHelper.saveProfileImage(imageFilePath!!)
        }
    }

    private fun saveImageToInternalStorage(imageUri: Uri): String? {
        val file = File(filesDir, "profile.jpg")
        return try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
