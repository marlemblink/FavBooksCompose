package com.example.favbookscompose.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.favbookscompose.data.DataOrException
import com.example.favbookscompose.model.MBook
import com.example.favbookscompose.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: FirebaseRepository) : ViewModel() {
    val data: MutableState<DataOrException<List<MBook>, Boolean, Exception>>
    = mutableStateOf(DataOrException(listOf(), true, Exception("")))
    init {
        getAllBooksFromDB()
    }

    private fun getAllBooksFromDB() {
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllBooksFromDatabase()
            if(data.value.data.isNullOrEmpty().not()) data.value.loading = false
        }
        Log.d("*******","getHomeVM: ${data.value.data?.toList().toString()}")
    }
}