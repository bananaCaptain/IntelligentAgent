package com.plantain.intelligentagent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.plantain.intelligentagent.data.model.ChatMessage
import com.plantain.intelligentagent.data.repository.ModelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(
    private val modelRepository: ModelRepository
) : ViewModel() {
    var message: String = "Shared ViewModel"

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _serviceStatusText = MutableStateFlow("未绑定服务")
    val serviceStatusText: StateFlow<String> = _serviceStatusText.asStateFlow()

    private val _serviceResultText = MutableStateFlow("")
    val serviceResultText: StateFlow<String> = _serviceResultText.asStateFlow()

    init {
        viewModelScope.launch {
            modelRepository.intelligentServiceBound.collectLatest { bound ->
                _serviceStatusText.value = if (bound) "服务已绑定" else "未绑定服务"
            }
        }
    }

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

    fun sendMessageToZai(text: String) {
        if (text.isBlank()) return
        val current = _messages.value.toMutableList()
        current.add(ChatMessage(text, true))
        _messages.value = current

        viewModelScope.launch {
            runCatching { modelRepository.chatZai(text) }
                .onSuccess { response ->
                    val reply = response.choices?.firstOrNull()?.message?.content ?: ""
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

    fun loadLocalModel(modelPath: String, nCtx: Int = 2048) {
        viewModelScope.launch {
            val success = modelRepository.loadLocalModel(modelPath, nCtx)
            val current = _messages.value.toMutableList()
            if (success) {
                current.add(ChatMessage("本地模型加载成功", false))
            } else {
                current.add(ChatMessage("本地模型加载失败", false))
            }
            _messages.value = current
        }
    }


    fun sendMessageToLocal(text: String) {
        if (text.isBlank()) return
        val current = _messages.value.toMutableList()
        current.add(ChatMessage(text, true))
        _messages.value = current

        viewModelScope.launch {
            runCatching { modelRepository.chatLocal(text) }
                .onSuccess { reply ->
                    val updated = _messages.value.toMutableList()
                    updated.add(ChatMessage(if (reply.isBlank()) "(empty)" else reply, false))
                    _messages.value = updated
                }
                .onFailure {
                    val updated = _messages.value.toMutableList()
                    updated.add(ChatMessage("本地模型请求失败", false))
                    _messages.value = updated
                }
        }
    }

    fun callServiceVerificationString() {
        viewModelScope.launch {
            runCatching { modelRepository.getServiceVerificationString() }
                .onSuccess { result ->
                    _serviceResultText.value = result
                }
                .onFailure { error ->
                    _serviceResultText.value = error.message ?: "调用失败：请先绑定服务"
                }
        }
    }

    fun getServiceLlamaSystemInfo() {
        viewModelScope.launch {
            runCatching { modelRepository.getServiceLlamaSystemInfo() }
                .onSuccess { info ->
                    _serviceResultText.value = info
                }
                .onFailure { error ->
                    _serviceResultText.value = error.message ?: "获取 Llama 系统信息失败"
                }
        }
    }

    fun loadServiceLlamaModel(modelPath: String, nCtx: Int = 2048) {
        if (modelPath.isBlank()) {
            _serviceResultText.value = "模型路径为空"
            return
        }
        viewModelScope.launch {
            runCatching { modelRepository.loadLlamaModelViaService(modelPath, nCtx) }
                .onSuccess { code ->
                    _serviceResultText.value = if (code == 0) {
                        "服务侧 Llama 模型加载成功"
                    } else {
                        "服务侧 Llama 模型加载失败，错误码: $code"
                    }
                }
                .onFailure { error ->
                    _serviceResultText.value = error.message ?: "服务侧 Llama 模型加载失败"
                }
        }
    }

    fun sendMessageToServiceLlama(text: String) {
        if (text.isBlank()) return
        val current = _messages.value.toMutableList()
        current.add(ChatMessage(text, true))
        _messages.value = current

        viewModelScope.launch {
            runCatching { modelRepository.chatWithLlamaViaService(text) }
                .onSuccess { reply ->
                    val updated = _messages.value.toMutableList()
                    updated.add(ChatMessage(if (reply.isBlank()) "(empty)" else reply, false))
                    _messages.value = updated
                }
                .onFailure {
                    val updated = _messages.value.toMutableList()
                    updated.add(ChatMessage("服务侧 Llama 请求失败", false))
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
