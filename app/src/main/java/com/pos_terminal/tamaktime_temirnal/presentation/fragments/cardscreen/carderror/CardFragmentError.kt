package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.carderror

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.CardState
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardErrorBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CardFragmentError : Fragment() {

    private var binding: FragmentCardErrorBinding by autoCleared()
    private val viewModel: CardFragmentViewModel by activityViewModels()

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cardState.collect { state ->
                    when (state) {
                        CardState.AUTHENTICATING_ERROR -> handleErrorState(
                            titleRes = R.string.smth_wrong_try_again,
                            imageRes = R.drawable.card_error,
                            onRepeatClick = {
                                findNavController().navigate(R.id.action_cardFragmentError_to_cardFragmentInitial)
                            },
                            showCancel = false
                        )

                        CardState.ORDER_ERROR -> handleErrorState(
                            titleRes = R.string.order_error,
                            imageRes = R.drawable.ordering_error,
                            onRepeatClick = { viewModel.ordering() },
                            showCancel = true
                        )

                        CardState.ORDERING -> {
                            findNavController().navigate(R.id.action_cardFragmentError_to_cardFragmentLoading)
                        }

                        CardState.INITIAL -> {
                            findNavController().navigate(R.id.action_cardFragmentError_to_cardFragmentInitial)
                        }

                        else -> {
                            Timber.w("Unexpected states: $state")
                        }
                    }
                }
            }
        }
    }

    private fun handleErrorState(
        @StringRes titleRes: Int,
        @DrawableRes imageRes: Int,
        onRepeatClick: () -> Unit,
        showCancel: Boolean,
    ) {
        Timber.d("Обработка состояния ошибки")
        binding.title.setText(titleRes)
        binding.image.setImageResource(imageRes)
        binding.buttonRepeat.setOnClickListener { onRepeatClick() }
        binding.buttonCancel.visibility = if (showCancel) View.VISIBLE else View.GONE
        if (showCancel) {
            binding.buttonCancel.setOnClickListener { viewModel.resetCardState() }
        }
    }

}