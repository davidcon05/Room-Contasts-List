package com.davecon.roomcontastslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(
    private val dao: ContactDao
) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    private val _contacts = _sortType.flatMapLatest { sortType ->
        when (sortType) {
            SortType.FIRST_NAME -> dao.getContactsOrderedByFirstName()
            SortType.LAST_NAME -> dao.getContactsOrdredByLastName()
            SortType.PHONE_NUMBER -> dao.getContactsOrdredByPhoneNumber()
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }

    private val _state = MutableStateFlow(ContactState())

    /**
     * Combine state, sortType and contacts flows into one state flow that can be observed
     * by the UI, with a timeout of 5 seconds and default value of ContactState(). If any of
     * these flows emit a new value, the state flow will be updated.
     */
    val state = combine(_state, _sortType, _contacts) { state, sortType, contacts ->
        state.copy(
            contacts = contacts,
            sortType = sortType
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    fun onEvent(event: ContactEvent) {
        when (event) {
            is ContactEvent.SetFirstName -> {
                _state.update { it.copy(firstName = event.firstName) }
            }

            is ContactEvent.SetLastName -> {
                _state.update { it.copy(lastName = event.lastName) }
            }

            is ContactEvent.SetPhoneNumber -> {
                _state.update { it.copy(phoneNumber = event.phoneNumber) }
            }

            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }

            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }

            /**
             * If any of the fields are blank, we don't want to save the contact. Otherwise,
             * we save the contact to the database and reset the state.
             */
            is ContactEvent.SaveContact -> {
                val firstName = _state.value.firstName
                val lastName = _state.value.lastName
                val phoneNumber = _state.value.phoneNumber
                val isMissingData =
                    firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()
                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )

                if (!isMissingData) {
                    viewModelScope.launch {
                        dao.upsertContact(contact)
                    }
                }

                _state.update {
                    it.copy(
                        isAddingContact = false,
                        firstName = "",
                        lastName = "",
                        phoneNumber = ""
                    )
                }
            }

            is ContactEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        isAddingContact = true
                    )
                }
            }

            is ContactEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingContact = false
                    )
                }
            }
        }
    }
}