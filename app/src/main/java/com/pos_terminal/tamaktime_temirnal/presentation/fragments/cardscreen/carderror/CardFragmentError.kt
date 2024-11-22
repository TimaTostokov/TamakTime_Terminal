package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.carderror

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardErrorBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed.SharedViewModel
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class CardFragmentError : Fragment() {

    private var binding: FragmentCardErrorBinding by autoCleared()
    private val viewModel: CardFragmentViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCancel.setOnClickListener {
            viewModel.resetCardState()
            sharedViewModel.resetOrder()
            sharedViewModel.resetUserAuthentication()
            findNavController().navigate(R.id.action_cardFragmentError_to_cardFragmentInitial)
        }
    }

}