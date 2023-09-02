package com.davecon.roomcontastslist

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This is a data class for referencing a contact in the database.
 */
@Entity
data class Contact (
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)