package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardinitial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardInitialBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed.SharedViewModel
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardFragmentInitial : Fragment() {

    private var _binding: FragmentCardInitialBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CardFragmentViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCardInitialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetState()
        binding.mrlBtnUnAuth.setOnClickListener {
            navigateToNextFragment()
        }

        binding.mrlBtnUnAuthQR.setOnClickListener {
            findNavController().navigate(R.id.action_cardFragmentInitial_to_qrOrderFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mrlBtnUnAuth.requestFocus()
        binding.mrlBtnUnAuthQR.requestFocus()
    }

    private fun navigateToNextFragment() {
        findNavController().navigate(
            R.id.action_cardFragmentInitial_to_cardFragmentLoading,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.cardFragmentInitial, true)
                .build()
        )
    }

    private fun resetState() {
        viewModel.resetCardState()
        sharedViewModel.resetOrder()
        sharedViewModel.resetUserAuthentication()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}