package com.pos_terminal.tamaktime_temirnal.presentation.fragments.dialoglogin

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.Resource
import com.pos_terminal.tamaktime_temirnal.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Credentials
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginDialogFragmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _loginForm = MutableStateFlow(LoginFormState())
    val loginFormState: StateFlow<LoginFormState> = _loginForm

    val hasCredentials: Flow<Boolean> = userRepository.flowHasCredentials()

    private fun isPhoneValid(phone: String) =
        Patterns.PHONE.matcher(phone).matches() && phone.first() == '+'

    private fun isPasswordValid(password: String) = password.length > 6

    fun loginDataChanged(phone: String, password: String) {
        val usernameError = if (!isPhoneValid(phone)) R.string.invalid_phone else null
        val passwordError = if (!isPasswordValid(password)) R.string.invalid_password else null

        _loginForm.value = LoginFormState(
            isDataValid = usernameError == null && passwordError == null,
            usernameError = usernameError,
            passwordError = passwordError
        )
    }

    fun authenticate(phone: String, password: String) {
        viewModelScope.launch {
            _loginForm.value = LoginFormState(isLoading = true)
            val credentials = Credentials.basic(phone, password)
            Timber.e("$phone | $password")
            Timber.e(credentials)

            val result = userRepository.me(credentials)

            when (result.status) {
                Resource.Status.SUCCESS -> {
                    val user = result.data?.copy(credentials = credentials)
                    Timber.e("User data: $user")

                    if (user != null && user.canteens.isNotEmpty()) {
                        Timber.e("First canteen role: ${user.canteens.first().role}")

                        if (user.canteens.first().role.equals("cashier", ignoreCase = true)) {
                            userRepository.saveUser(user)
                            _loginForm.value = LoginFormState(isLoading = false)
                        } else {
                            _loginForm.value = LoginFormState(
                                isLoading = false,
                                usernameError = R.string.vhod_cashier
                            )
                            Timber.e("Access denied for user with role: ${user.canteens.first().role}")
                        }
                    } else {
                        _loginForm.value = LoginFormState(
                            isLoading = false,
                            usernameError = R.string.vhod_cashier
                        )
                        Timber.e("User has no canteen roles or user is null")
                    }
                }

                Resource.Status.LOADING -> {
                    Timber.d("Loading user data...")
                    _loginForm.value = LoginFormState(isLoading = true)
                }

                Resource.Status.ERROR -> {
                    _loginForm.value = LoginFormState(isLoading = false)
                    Timber.e("${result.status.name} | ${result.message}")
                }
            }
        }
    }

}