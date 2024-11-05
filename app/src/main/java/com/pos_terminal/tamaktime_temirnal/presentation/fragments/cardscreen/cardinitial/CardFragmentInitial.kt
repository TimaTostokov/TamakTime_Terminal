package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardinitial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardInitialBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardFragmentInitial : Fragment() {

    private var binding: FragmentCardInitialBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCardInitialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mrlBtnUnAuth.setOnClickListener {
            findNavController().navigate(R.id.action_cardFragmentInitial_to_cardFragmentLoading)
        }

        binding.mrlBtnUnAuthQR.setOnClickListener {
            findNavController().navigate(R.id.action_cardFragmentInitial_to_qrOrderFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mrlBtnUnAuth.requestFocus()
    }

}