package com.sagorika.bookhub.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class], version = 1)
abstract class BookDatabase: RoomDatabase() {

    //to tell that functions we'll perform on the data will be performed by the DAO interface
    abstract fun bookDao(): BookDao
}