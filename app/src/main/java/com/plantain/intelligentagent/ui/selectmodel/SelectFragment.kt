package com.plantain.intelligentagent.ui.selectmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.plantain.intelligentagent.R
import com.plantain.intelligentagent.databinding.FragmentSelectBinding
import com.plantain.intelligentagent.ui.MainViewModel

class SelectFragment : Fragment(R.layout.fragment_select) {

    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSelectBinding.bind(view)
        binding.textFirst.text = sharedViewModel.message
        binding.btnToSecond.setOnClickListener {
            findNavController().navigate(R.id.action_first_to_second)
        }
    }

}
