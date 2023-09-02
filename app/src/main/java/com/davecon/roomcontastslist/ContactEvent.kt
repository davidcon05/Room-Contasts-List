package com.davecon.roomcontastslist

sealed interface ContactEvent {
    data class SetFirstName(val firstName: String) : ContactEvent
    data class SetLastName(val lastName: String) : ContactEvent
    data class SetPhoneNumber(val phoneNumber: String) : ContactEvent
    data class SortContacts(val sortType: SortType) : ContactEvent
    data class DeleteContact(val contact: Contact) : ContactEvent
    object SaveContact : ContactEvent
    object ShowDialog : ContactEvent
    object HideDialog : ContactEvent
}