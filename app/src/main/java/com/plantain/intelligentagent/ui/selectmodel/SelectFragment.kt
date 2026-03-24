package com.plantain.intelligentagent.ui.selectmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.plantain.intelligentagent.R
import com.plantain.intelligentagent.databinding.FragmentSelectBinding
import com.plantain.intelligentagent.ui.MainViewModel
import kotlinx.coroutines.launch

class SelectFragment : Fragment(R.layout.fragment_select) {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textFirst.text = sharedViewModel.message
        binding.tvServiceStatus.text = "未绑定服务"
        binding.tvServiceResult.text = ""

        binding.btnCallService.setOnClickListener {
            sharedViewModel.callServiceVerificationString()
        }

        binding.btnToSecond.setOnClickListener {
            findNavController().navigate(R.id.action_first_to_second)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    sharedViewModel.serviceStatusText.collect { status ->
                        binding.tvServiceStatus.text = status
                    }
                }
                launch {
                    sharedViewModel.serviceResultText.collect { result ->
                        binding.tvServiceResult.text = result
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}
