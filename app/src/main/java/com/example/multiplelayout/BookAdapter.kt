package com.example.multiplelayout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BookAdapter(
    private val context: Context,
    private var booksList: MutableList<Book>,
    private val onEditClick: (Book) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (Book) -> Unit // Tambahkan parameter ini
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookName: TextView = itemView.findViewById(R.id.bookName)
        val bookNickname: TextView = itemView.findViewById(R.id.bookNickname)
        val bookEmail: TextView = itemView.findViewById(R.id.bookEmail)
        val bookAddress: TextView = itemView.findViewById(R.id.bookAddress)
        val bookPhone: TextView = itemView.findViewById(R.id.bookPhone)
        val bookBirthdate: TextView = itemView.findViewById(R.id.bookBirthdate)
        val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
        val editButton: ImageView = itemView.findViewById(R.id.edit_book)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_book)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = booksList[position]

        holder.bookName.text = book.name
        holder.bookNickname.text = book.nickname
        holder.bookEmail.text = book.email
        holder.bookAddress.text = book.address
        holder.bookPhone.text = book.phone
        holder.bookBirthdate.text = book.birthdate

        // Load image using Glide
        Glide.with(context)
            .load(book.imagePath)
            .placeholder(R.drawable.circular_background) // Default placeholder image
            .into(holder.bookImage)

        // Edit button click
        holder.editButton.setOnClickListener {
            onEditClick(book)
        }

        // Delete button click
        holder.deleteButton.setOnClickListener {
            onDeleteClick(book.id)
        }

        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    override fun getItemCount(): Int {
        return booksList.size
    }
}
