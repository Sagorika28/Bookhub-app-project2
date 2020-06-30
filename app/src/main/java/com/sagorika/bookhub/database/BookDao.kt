package com.sagorika.bookhub.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.sql.RowId

@Dao
interface BookDao {
    @Insert
    fun insertBook(bookEntity: BookEntity)

    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * FROM books")
    fun getAllBooks(): List<BookEntity>

    //to check whether a book is present in fav or not
    @Query("SELECT * FROM books WHERE book_id = :bookId")
    fun getBookById(bookId: String): BookEntity
}