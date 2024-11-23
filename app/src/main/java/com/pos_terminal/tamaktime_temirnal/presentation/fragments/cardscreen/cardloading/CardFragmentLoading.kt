package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardloading

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.airbnb.lottie.LottieAnimationView
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.CardState
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardLoadingBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed.SharedViewModel
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardFragmentLoading : Fragment(), CardFragmentViewModel.CardNavigationListener {

    private var binding: FragmentCardLoadingBinding by autoCleared()

    private val viewModel: CardFragmentViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var lottieAnimationView: LottieAnimationView

    private val handler = Handler(Looper.getMainLooper())
    private val navigateToErrorRunnable = Runnable {
        navigateToErrorFragment()
    }

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

        resetState()

        lottieAnimationView = binding.progress
        lottieAnimationView.playAnimation()

        if (!viewModel.isTimerRunning) {
            startInactivityTimer()
        }
//        startCardAuthentication()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.student.collect { student ->
                    if (student != null) {
                        cancelInactivityTimer()
                        navigateToCategories()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cardState.collect { state ->
                    when (state) {
                        CardState.AUTHENTICATING -> {
                            binding.title.text = resources.getString(R.string.card_reading_wait)
                            lottieAnimationView.playAnimation()
                        }

                        CardState.AUTHENTICATED -> {
//                            viewModel.mockupOrdering()
                            cancelInactivityTimer()
                            lottieAnimationView.cancelAnimation()
                            navigateToCategories()
                        }

                        CardState.AUTHENTICATING_ERROR -> {
                            cancelInactivityTimer()
                            lottieAnimationView.cancelAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Пользователь не найден",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_cardFragmentLoading_to_cardFragmentError)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

//    private fun startCardAuthentication() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            delay(3000)
//            val simulatedCardUuid = "62A2742E"
//            handleNfcTag(simulatedCardUuid)
//        }
//    }

    private fun startInactivityTimer() {
        viewModel.isTimerRunning = true
        handler.postDelayed(navigateToErrorRunnable, 20000)
    }

    private fun cancelInactivityTimer() {
        handler.removeCallbacks(navigateToErrorRunnable)
        viewModel.isTimerRunning = false
    }

    private fun navigateToErrorFragment() {
        findNavController().navigate(R.id.action_cardFragmentLoading_to_cardFragmentError)
    }

    fun handleNfcTag(cardUuid: String) {
        cancelInactivityTimer()
        viewModel.setCardUuid(cardUuid)
        viewModel.authenticateCard(sharedViewModel)
    }

    override fun navigateToCategories() {
        kotlin.runCatching {
            findNavController().navigate(R.id.action_cardFragmentLoading_to_cardFragmentAuthed)
        }
    }

    private fun resetState() {
        viewModel.resetCardState()
        sharedViewModel.resetOrder()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancelInactivityTimer()
    }

}