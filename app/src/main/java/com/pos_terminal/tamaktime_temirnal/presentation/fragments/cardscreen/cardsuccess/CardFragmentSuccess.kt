package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardsuccess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.viewBinding
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardSuccessBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed.SharedViewModel
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardFragmentSuccess : Fragment(R.layout.fragment_card_success) {

    private val binding by viewBinding(FragmentCardSuccessBinding::bind)

    private val viewModel: CardFragmentViewModel by activityViewModels()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navAuth.setOnClickListener {
            resetState()
            findNavController().navigate(R.id.action_cardFragmentSuccess_to_cardFragmentInitial)
        }
    }
    private fun resetState() {
        viewModel.resetCardState()
        sharedViewModel.resetOrder()
        sharedViewModel.resetUserAuthentication()
    }

}