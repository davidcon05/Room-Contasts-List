package com.davecon.roomcontastslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactViewModel(
    private val dao: ContactDao
) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    private val _state = MutableStateFlow(ContactState())

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

            is ContactEvent.SaveContact -> {
                val firstName = _state.value.firstName
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