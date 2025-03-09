package com.example.multiplelayout

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailBookAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_book)

        // Use getParcelableExtra instead of getSerializableExtra
        val book = intent.getParcelableExtra<Book>("BOOK_DETAIL")

        findViewById<TextView>(R.id.detailBookName).text = book?.name
        findViewById<TextView>(R.id.detailBookNickname).text = book?.nickname
        findViewById<TextView>(R.id.detailBookEmail).text = book?.email
        findViewById<TextView>(R.id.detailBookAddress).text = book?.address
        findViewById<TextView>(R.id.detailBookPhone).text = book?.phone
        findViewById<TextView>(R.id.detailBookBirthdate).text = book?.birthdate

        val bookImage = findViewById<ImageView>(R.id.detailBookImage)
        book?.imagePath?.let {
            Log.d("DetailBookAct", "Image Path: $it")
            Glide.with(this).load(it).into(bookImage)
        } ?: Log.d("DetailBookAct", "Image Path is null or empty")

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}
