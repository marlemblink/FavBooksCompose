package com.example.favbookscompose.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.favbookscompose.data.Resource
import com.example.favbookscompose.model.Item
import com.example.favbookscompose.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksSearchViewModel @Inject constructor(private val repository: BooksRepository): ViewModel() {
    var list: List<Item> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(true)

    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBooks("harrypotter")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.Default){
            //isLoading = true issue related with that and dispatcher
            if(query.isEmpty()) return@launch
            try {
                when(val response = repository.getBooks(query)) {
                    is Resource.Success -> {
                        list = response.data ?: emptyList()
                        if(list.isNotEmpty()) isLoading = false
                    }
                    is Resource.Error -> Log.d("*******","SearchVM: Failed getting books")
                    else -> {}
                }
            } catch (e: Exception) {
                isLoading = false
                Log.d("*******","${e.message.toString()}")
            }
        }
    }
}