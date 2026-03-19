package com.plantain.intelligentagent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.plantain.intelligentagent.data.model.ChatMessage
import com.plantain.intelligentagent.data.repository.ModelRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val modelRepository: ModelRepository
) : ViewModel() {
    var message: String = "Shared ViewModel"

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val current = _messages.value.toMutableList()
        current.add(ChatMessage(text, true))
        _messages.value = current

        viewModelScope.launch {
            runCatching { modelRepository.chat(text) }
                .onSuccess { response ->
                    val reply = response.output?.text ?: ""
                    val updated = _messages.value.toMutableList()
                    updated.add(ChatMessage(if (reply.isBlank()) "(empty)" else reply, false))
                    _messages.value = updated
                }
                .onFailure {
                    val updated = _messages.value.toMutableList()
                    updated.add(ChatMessage("请求失败", false))
                    _messages.value = updated
                }
        }
    }


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
