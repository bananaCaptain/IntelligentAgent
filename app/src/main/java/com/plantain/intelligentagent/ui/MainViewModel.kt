package com.plantain.intelligentagent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.plantain.intelligentagent.data.repository.ModelRepository

class MainViewModel(
    private val modelRepository: ModelRepository
) : ViewModel() {
    var message: String = "Shared ViewModel"


    companion object {
        fun getMainViewModelFactory(modelRepository: ModelRepository)
                : ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                        return MainViewModel(modelRepository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
