package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardloading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.CardState
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardLoadingBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardFragmentLoading : Fragment(
), CardFragmentViewModel.CardNavigationListener {

    private var binding: FragmentCardLoadingBinding by autoCleared()

    private val viewModel: CardFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCardLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.authenticateCard()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.student.collect { student ->
                    if (student != null) {
                        navigateToCategories()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cardState.distinctUntilChanged { old, new -> old == new }
                    .collect { state ->
                        when (state) {
                            CardState.AUTHENTICATING -> {
                                binding.title.text = resources.getString(R.string.card_reading_wait)
                            }

                            CardState.AUTHENTICATED -> {
                            }

                            CardState.ORDERING -> {
                                binding.title.text = resources.getString(R.string.ordering)
                            }
                            CardState.ORDER_SUCCESS -> {
                                navigateToCategories()
                            }

                            CardState.AUTHENTICATING_ERROR -> {
                                findNavController().navigate(R.id.action_cardFragmentLoading_to_cardFragmentError)

                                if (viewModel.student.value == null) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Студент не найден",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }

                            else -> {
                            }
                        }
                    }
            }
        }
    }

    fun handleNfcTag(cardUuid: String) {
        viewModel.setCardUuid(cardUuid)
        viewModel.authenticateCard()
    }

    override fun navigateToCategories() {
        kotlin.runCatching {
            findNavController().navigate(R.id.action_cardFragmentLoading_to_cardFragmentAuthed)
        }
    }

}