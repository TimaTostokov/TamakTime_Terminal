package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardloading

import android.os.Bundle
import android.util.Log
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
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.Student
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardLoadingBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed.SharedViewModel
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QrOrderFragmentLoading : Fragment(), CardFragmentViewModel.CardNavigationListener {

    private var binding: FragmentCardLoadingBinding by autoCleared()

    private val viewModel: CardFragmentViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

        fun onQrCodeScanned(qrCode: String) {
            handleQrTag(qrCode)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.studentQR.collect { qrOrderItem ->
                    if (qrOrderItem != null) {
                        val testItem = Student(
                            qrOrderItem.creator.id,
                            qrOrderItem.creator.email,
                            qrOrderItem.creator.firstName,
                            qrOrderItem.creator.lastName,
                            qrOrderItem.creator.birthDate,
                            qrOrderItem.creator.phone,
                            qrOrderItem.creator.cardUuid,
                            qrOrderItem.creator.isActive,
                            qrOrderItem.creator.gender,
                            qrOrderItem.creator.balance,
                            qrOrderItem.creator.group,
                            qrOrderItem.creator.school,
                            qrOrderItem.creator.photo
                        )
                        val student = testItem
                        Log.d("QrOrderFragmentLoading", "Student found: $student")
                        Log.e("studentResult", "Result: $student")
                        navigateToCategories()
                    } else {
                        Log.d("QrOrderFragmentLoading", "studentQR is null")
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
                                lottieAnimationView.playAnimation()
                            }

                            CardState.AUTHENTICATED -> {
                                lottieAnimationView.cancelAnimation()
                                navigateToCategories()
                            }

                            CardState.ORDERING -> {
                                binding.title.text = resources.getString(R.string.ordering)
                            }

                            CardState.AUTHENTICATING_ERROR -> {
                                lottieAnimationView.cancelAnimation()
                                findNavController().navigate(R.id.action_qrOrderFragmentLoading_to_cardFragmentError)
                                Toast.makeText(
                                    requireContext(),
                                    "Студент не найден",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {}
                        }
                    }
            }
        }
    }

    fun handleQrTag(cardUuid: String) {
        viewModel.setCardUuid(cardUuid)
        viewModel.authenticateStudentByQR(cardUuid,sharedViewModel)
        viewModel.authenticateCard(sharedViewModel)
    }

    override fun navigateToCategories() {
        kotlin.runCatching {
            findNavController().navigate(R.id.action_qrOrderFragmentLoading_to_cardFragmentAuthed)
        }
    }

    private fun resetState() {
        viewModel.resetCardState()
        sharedViewModel.resetOrder()
    }

}