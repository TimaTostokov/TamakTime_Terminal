package com.pos_terminal.tamaktime_temirnal.presentation.fragments.dialoglogin

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentLoginDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginDialogFragment : DialogFragment() {

    private var binding: FragmentLoginDialogBinding by autoCleared()

    private val viewModel: LoginDialogFragmentViewModel by viewModels()

    private fun toError(
        input: TextInputEditText, lyt: TextInputLayout,
        text: String?,
    ) {
        input.error = text
        lyt.error = text
        lyt.errorIconDrawable = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.hasCredentials.collect { hasCredentials ->
                if (hasCredentials) dialog?.dismiss()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginFormState.collect { state ->
                    with(binding) {
                        toError(
                            phoneInput,
                            phoneInputLyt,
                            state.usernameError?.let { getString(it) })
                        toError(
                            passwordInput,
                            passwordInputLyt,
                            state.passwordError?.let { getString(it) })

                        loginButton.isEnabled = !state.isLoading && state.isDataValid
                        progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    }
                }
            }
        }

        val afterTextChangedListener: (Editable?) -> Unit = {
            val phone = binding.phoneInput.text.toString()
            val password = binding.passwordInput.text.toString()
            viewModel.loginDataChanged(phone, password)
        }

        with(binding) {
            arrayOf(phoneInput, passwordInput).forEach { input ->
                input.addTextChangedListener(afterTextChanged = afterTextChangedListener)
            }

            passwordInput.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.authenticate(
                        phoneInput.text.toString(),
                        passwordInput.text.toString()
                    )
                }
                false
            }

            loginButton.setOnClickListener {
                viewModel.authenticate(
                    phoneInput.text.toString(),
                    passwordInput.text.toString()
                )
            }
        }
    }

}