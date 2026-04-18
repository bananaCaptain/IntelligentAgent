package com.plantain.intelligentagent.ui.executionmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.plantain.intelligentagent.R
import com.plantain.intelligentagent.data.model.ChatMessage
import com.plantain.intelligentagent.databinding.FragmentExecutionBinding
import com.plantain.intelligentagent.ui.MainViewModel
import com.drake.softinput.setWindowSoftInput
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.launch
import java.util.Locale

class ExecutionFragment : Fragment(R.layout.fragment_execution) {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentExecutionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExecutionBinding.bind(view)

        // Float only the input bar; avoid translating the full fragment root.
        setWindowSoftInput(float = binding.inputBar, transition = binding.inputBar)

        binding.rvChat.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }

        binding.rvChat.linear().setup {
            addType<ChatMessage>(R.layout.item_chat_message)
            onBind {
                val message = getModel<ChatMessage>()
                val userBubble = findView<LinearLayout>(R.id.userBubble)
                val aiBubble = findView<LinearLayout>(R.id.aiBubble)
                val userText = findView<TextView>(R.id.tvUserMessage)
                val aiText = findView<TextView>(R.id.tvAiMessage)
                val aiLoading = findView<ProgressBar>(R.id.aiLoading)
                val aiInferenceTime = findView<TextView>(R.id.tvAiInferenceTime)

                if (message.isUser) {
                    userBubble.visibility = View.VISIBLE
                    aiBubble.visibility = View.GONE
                    userText.text = message.content
                } else {
                    userBubble.visibility = View.GONE
                    aiBubble.visibility = View.VISIBLE
                    if (message.isLoading) {
                        aiLoading.visibility = View.VISIBLE
                        aiText.text = "推理中..."
                        aiInferenceTime.visibility = View.GONE
                    } else {
                        aiLoading.visibility = View.GONE
                        aiText.text = message.content
                        if (message.inferenceSeconds != null) {
                            aiInferenceTime.visibility = View.VISIBLE
                            aiInferenceTime.text = String.format(
                                Locale.US,
                                "推理耗时 %.3f s",
                                message.inferenceSeconds
                            )
                        } else {
                            aiInferenceTime.visibility = View.GONE
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.messages.collect { list ->
                    binding.rvChat.models = list
                    if (list.isNotEmpty()) {
                        binding.rvChat.scrollToPosition(list.size - 1)
                    }
                }
            }
        }

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isBlank()) return@setOnClickListener
//            sharedViewModel.sendMessage(text)
        //    sharedViewModel.sendMessageToZai(text)
//            sharedViewModel.sendMessageToLocal(text)
            sharedViewModel.sendMessageToServiceLlama(text)
            binding.etMessage.text?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
