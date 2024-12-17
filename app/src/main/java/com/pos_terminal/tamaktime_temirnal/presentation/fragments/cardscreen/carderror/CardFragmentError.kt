package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.carderror

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.viewBinding
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardErrorBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed.SharedViewModel
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardFragmentError : Fragment(R.layout.fragment_card_error) {

    private val binding by viewBinding(FragmentCardErrorBinding::bind)
    private val viewModel: CardFragmentViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

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