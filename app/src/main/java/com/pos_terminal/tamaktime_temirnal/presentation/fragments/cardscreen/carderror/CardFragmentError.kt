package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.carderror

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardErrorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardFragmentError : Fragment() {

    private var binding: FragmentCardErrorBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCardErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}