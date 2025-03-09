package com.example.multiplelayout

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multiplelayout.databinding.ActivityBooksBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BooksAct : AppCompatActivity() {

    private lateinit var binding: ActivityBooksBinding
    private lateinit var bookAdapter: BookAdapter
    private var booksList = mutableListOf<Book>() // Mutable list to store books

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        setupRecyclerView()

        // Button Listeners
        binding.aboutBtn.setOnClickListener {
            val intent = Intent(this, AboutBookAct::class.java)
            startActivity(intent)
        }

        binding.addBookbtn.setOnClickListener {
            val intent = Intent(this, AddBookAct::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        // Load Books
        lifecycleScope.launch {
            loadBooksFromDatabase()
        }

    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(this, booksList, onEditClick = { book ->
            val intent = Intent(this, EditBookAct::class.java)
            intent.putExtra("BOOK_ID", book.id)
            startActivity(intent)
        }, onDeleteClick = { bookId ->
            showDeleteConfirmationDialog(bookId)
        }, onItemClick = { selectedBook ->
            val intent = Intent(this, DetailBookAct::class.java)
            intent.putExtra("BOOK_DETAIL", selectedBook) // No need to cast!
            startActivity(intent)
        })

        binding.recyclerViewBooks.apply {
            layoutManager = LinearLayoutManager(this@BooksAct)
            adapter = bookAdapter
        }
    }

    private fun showDeleteConfirmationDialog(bookId: Int) {
        AlertDialog.Builder(this).apply {
            setTitle("Hapus Buku")
            setMessage("Yakin mau hapus buku ini?")
            setPositiveButton("Iya") { _, _ ->
                deleteBook(bookId)
            }
            setNegativeButton("Tidak", null) // Just dismiss the dialog
            show()
        }
    }

    private fun deleteBook(bookId: Int) {
        val databaseHelper = DatabaseHelper(this)
        val deletedRows = databaseHelper.deleteBook(bookId)

        if (deletedRows > 0) {
            booksList.removeIf { it.id == bookId } // Remove book from the list
            bookAdapter.notifyDataSetChanged() // Notify RecyclerView

            Toast.makeText(this, "Buku berhasil dihapus", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun loadBooksFromDatabase() {
        withContext(Dispatchers.IO) {
            val databaseHelper = DatabaseHelper(this@BooksAct)
            val books = databaseHelper.getAllBooks()
            withContext(Dispatchers.Main) {
                booksList.clear()
                booksList.addAll(books)
                bookAdapter.notifyDataSetChanged()
            }
        }
    }
}
