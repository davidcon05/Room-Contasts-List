package com.davecon.roomcontastslist

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * This abstract class allows us to interact with the database using a DAO
 */
@Database(entities = [Contact::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {
    abstract val dao: ContactDao
}