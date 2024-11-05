package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardsuccess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.CardState
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardSuccessBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CardFragmentSuccess : Fragment() {

    private var binding: FragmentCardSuccessBinding by autoCleared()
    private val viewModel: CardFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCardSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonOk.setOnClickListener {
            Timber.e("OK CLICKED")
            viewModel.resetCardState()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cardState.collect { state ->
                    if (state == CardState.INITIAL) {
                        findNavController().navigate(R.id.action_cardFragmentSuccess_to_cardFragmentInitial)
                    }
                }
            }
        }
    }

}