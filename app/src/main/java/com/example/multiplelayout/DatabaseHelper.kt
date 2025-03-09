package com.example.multiplelayout

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app.db"
        private const val DATABASE_VERSION = 3  // Updated version for book storage

        // Users Table
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USER_USERNAME = "username"
        private const val COLUMN_USER_PASSWORD = "password"

        // Notes Table
        private const val TABLE_NOTES = "notes"
        private const val COLUMN_NOTE_ID = "id"
        private const val COLUMN_NOTE_TITLE = "title"
        private const val COLUMN_NOTE_CONTENT = "content"

        // Profile Images Table
        private const val TABLE_PROFILE_IMAGES = "profile_images"
        private const val COLUMN_PROFILE_ID = "id"
        private const val COLUMN_IMAGE_PATH = "image_path"

        // Books Table (New)
        private const val TABLE_BOOKS = "books"
        private const val COLUMN_BOOK_ID = "id"
        private const val COLUMN_BOOK_NAME = "name"
        private const val COLUMN_BOOK_NICKNAME = "nickname"
        private const val COLUMN_BOOK_EMAIL = "email"
        private const val COLUMN_BOOK_ADDRESS = "address"
        private const val COLUMN_BOOK_PHONE = "phone"
        private const val COLUMN_BOOK_BIRTHDATE = "birthdate"
        private const val COLUMN_BOOK_IMAGE_PATH = "image_path"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $COLUMN_USER_USERNAME TEXT, 
                $COLUMN_USER_PASSWORD TEXT
            )
        """.trimIndent()
        db?.execSQL(createUsersTable)

        val createNotesTable = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $COLUMN_NOTE_TITLE TEXT, 
                $COLUMN_NOTE_CONTENT TEXT
            )
        """.trimIndent()
        db?.execSQL(createNotesTable)

        val createProfileImagesTable = """
            CREATE TABLE $TABLE_PROFILE_IMAGES (
                $COLUMN_PROFILE_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $COLUMN_IMAGE_PATH TEXT
            )
        """.trimIndent()
        db?.execSQL(createProfileImagesTable)

        val createBooksTable = """
            CREATE TABLE $TABLE_BOOKS (
                $COLUMN_BOOK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BOOK_NAME TEXT NOT NULL,
                $COLUMN_BOOK_NICKNAME TEXT NOT NULL,
                $COLUMN_BOOK_EMAIL TEXT NOT NULL,
                $COLUMN_BOOK_ADDRESS TEXT,
                $COLUMN_BOOK_PHONE TEXT NOT NULL,
                $COLUMN_BOOK_BIRTHDATE TEXT NOT NULL,
                $COLUMN_BOOK_IMAGE_PATH TEXT
            )
        """.trimIndent()
        db?.execSQL(createBooksTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_PROFILE_IMAGES ($COLUMN_PROFILE_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_IMAGE_PATH TEXT)")
        }
        if (oldVersion < 3) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
            onCreate(db)
        }
    }

    // ========================== PROFILE IMAGE FUNCTIONS ==========================
    fun saveProfileImage(imagePath: String) {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PROFILE_IMAGES LIMIT 1", null)
        val values = ContentValues().apply {
            put(COLUMN_IMAGE_PATH, imagePath)
        }

        if (cursor.count > 0) {
            db.update(TABLE_PROFILE_IMAGES, values, null, null)
        } else {
            db.insert(TABLE_PROFILE_IMAGES, null, values)
        }

        cursor.close()
        db.close()
    }

    fun getProfileImage(): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_IMAGE_PATH FROM $TABLE_PROFILE_IMAGES LIMIT 1", null)

        val imagePath: String? = if (cursor.moveToFirst()) {
            cursor.getString(0)
        } else null

        cursor.close()
        db.close()
        return imagePath
    }

    // ========================== USER FUNCTIONS ==========================
    fun insertUser(username: String, password: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_USER_USERNAME, username)
            put(COLUMN_USER_PASSWORD, password)
        }
        val db = writableDatabase
        return db.insert(TABLE_USERS, null, values)
    }

    fun readUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USER_USERNAME = ? AND $COLUMN_USER_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    fun isUserAvailable(): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_USERS"
        val cursor = db.rawQuery(query, null)
        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()

        return count > 0
    }

    // ========================== NOTE FUNCTIONS ==========================
    fun insertNote(note: Note) {
        val values = ContentValues().apply {
            put(COLUMN_NOTE_TITLE, note.title)
            put(COLUMN_NOTE_CONTENT, note.content)
        }
        val db = writableDatabase
        db.insert(TABLE_NOTES, null, values)
        db.close()
    }

    fun updateNote(note: Note) {
        val values = ContentValues().apply {
            put(COLUMN_NOTE_TITLE, note.title)
            put(COLUMN_NOTE_CONTENT, note.content)
        }
        val db = writableDatabase
        val whereClause = "$COLUMN_NOTE_ID = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NOTES, values, whereClause, whereArgs)
        db.close()
    }

    fun deleteNote(noteId: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_NOTE_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NOTES, whereClause, whereArgs)
        db.close()
    }


    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NOTES"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT))

            val note = Note(id, title, content)
            notes.add(note)
        }
        cursor.close()
        db.close()
        return notes
    }

    fun getNoteById(noteId: Int): Note {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NOTES WHERE $COLUMN_NOTE_ID = $noteId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT))

        cursor.close()
        db.close()
        return Note(id, title, content)
    }

    // ========================== BOOK FUNCTIONS ==========================
    fun insertBook(book: Book): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BOOK_NAME, book.name)
            put(COLUMN_BOOK_NICKNAME, book.nickname)
            put(COLUMN_BOOK_EMAIL, book.email)
            put(COLUMN_BOOK_ADDRESS, book.address)
            put(COLUMN_BOOK_PHONE, book.phone)
            put(COLUMN_BOOK_BIRTHDATE, book.birthdate)
            put(COLUMN_BOOK_IMAGE_PATH, book.imagePath)
        }

        val result = db.insert(TABLE_BOOKS, null, values)
        db.close()
        return result
    }

    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_BOOKS"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_NAME))
            val nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_NICKNAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_EMAIL))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ADDRESS))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_PHONE))
            val birthdate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_BIRTHDATE))
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_IMAGE_PATH))

            books.add(Book(id, name, nickname, email, address, phone, birthdate, imagePath))
        }
        cursor.close()
        db.close()
        return books
    }

    fun deleteBook(bookId: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_BOOKS, "$COLUMN_BOOK_ID = ?", arrayOf(bookId.toString()))
        db.close()
        return result
    }

    fun getBookById(bookId: Int): Book? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_BOOKS WHERE $COLUMN_BOOK_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(bookId.toString()))

        var book: Book? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_NAME))
            val nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_NICKNAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_EMAIL))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_ADDRESS))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_PHONE))
            val birthdate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_BIRTHDATE))
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOK_IMAGE_PATH))

            book = Book(id, name, nickname, email, address, phone, birthdate, imagePath)
        }

        cursor.close()
        db.close()
        return book
    }

    fun updateBook(book: Book): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BOOK_NAME, book.name)
            put(COLUMN_BOOK_NICKNAME, book.nickname)
            put(COLUMN_BOOK_EMAIL, book.email)
            put(COLUMN_BOOK_ADDRESS, book.address)
            put(COLUMN_BOOK_PHONE, book.phone)
            put(COLUMN_BOOK_BIRTHDATE, book.birthdate)
            put(COLUMN_BOOK_IMAGE_PATH, book.imagePath)
        }

        val result = db.update(
            TABLE_BOOKS, values, "$COLUMN_BOOK_ID = ?",
            arrayOf(book.id.toString())
        )

        db.close()
        return result
    }
}
