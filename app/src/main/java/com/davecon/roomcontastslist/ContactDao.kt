package com.davecon.roomcontastslist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * This is a data access object for the database, it contains all the functions for
 * accessing the Room database.The annotations are used to generate the code for the functions.
 * By using the suspend keyword we can call the functions from a coroutine.
 */
@Dao
interface ContactDao {

    @Upsert
    suspend fun upsertContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * FROM Contact ORDER BY firstName ASC")
    fun getContactsOrderedByFirstName(): Flow<List<Contact>>

    @Query("SELECT * FROM Contact ORDER BY lastName ASC")
    fun getContactsOrdredByLastName(): Flow<List<Contact>>

    @Query("SELECT * FROM Contact ORDER BY phoneNumber ASC")
    fun getContactsOrdredByPhoneNumber(): Flow<List<Contact>>
}