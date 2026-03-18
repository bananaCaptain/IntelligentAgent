package com.plantain.intelligentagent.ui.executionmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.plantain.intelligentagent.R
import com.plantain.intelligentagent.databinding.FragmentExecutionBinding
import com.plantain.intelligentagent.ui.MainViewModel

class ExecutionFragment : Fragment(R.layout.fragment_execution) {

    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentExecutionBinding.bind(view)
        binding.textSecond.text = sharedViewModel.message
        binding.btnToFirst.setOnClickListener {
            findNavController().navigateUp()
        }
    }


}
