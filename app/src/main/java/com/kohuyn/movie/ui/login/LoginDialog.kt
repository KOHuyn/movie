package com.kohuyn.movie.ui.login

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kohuyn.movie.R
import com.kohuyn.movie.databinding.DialogLoginBinding
import com.kohuyn.movie.ui.alert.MessageDialog
import com.kohuyn.movie.utils.createDialog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginDialog : DialogFragment() {

    private lateinit var binding: DialogLoginBinding
    private val vm by viewModels<LoginViewModel>()

    private val edtUserNameStateFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val edtPasswordStateFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private var onLoginSuccess: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.createDialog()
    }

    override fun isCancelable(): Boolean {
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleListener()
        observerViewModel()
    }

    private fun handleListener() {
        //default value
        edtUserNameStateFlow.update { binding.edtUserNameForm.text?.toString() }
        edtPasswordStateFlow.update { binding.edtPasswordForm.text?.toString() }
        binding.edtUserNameForm.doOnTextChanged { text, _, _, _ ->
            edtUserNameStateFlow.update { text?.toString() }
        }
        binding.edtPasswordForm.doOnTextChanged { text, _, _, _ ->
            edtPasswordStateFlow.update { text?.toString() }
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.btnLogin.setOnClickListener {
            val userName = edtUserNameStateFlow.value
            val password = edtPasswordStateFlow.value
            vm.login(userName ?: "", password ?: "")
        }
        vm.onLoginSuccess = {
            onLoginSuccess()
        }
        combine(edtUserNameStateFlow, edtPasswordStateFlow) { userName, password ->
            val isEnableLogin = (userName.isNullOrBlank() || password.isNullOrBlank()).not()
            binding.btnLogin.isEnabled = isEnableLogin
            binding.btnLogin.backgroundTintList =
                if (isEnableLogin) null else ColorStateList.valueOf(Color.GRAY)
        }.launchIn(lifecycleScope)
    }

    private fun observerViewModel() {
        lifecycleScope.launch {
            vm.isLoading
                .flowWithLifecycle(lifecycle)
                .collect { isLoading ->
                    binding.btnLogin.text = if (isLoading) null else getString(R.string.login)
                    binding.progressLoading.isVisible = isLoading
                    binding.btnLogin.isEnabled = isLoading.not()
                }
        }

        lifecycleScope.launch {
            vm.messages
                .flowWithLifecycle(lifecycle)
                .collect { messages ->
                    if (messages.isNotEmpty()) {
                        val messageShow = messages.first()
                        MessageDialog.Builder()
                            .setMessage(messageShow)
                            .setButtonNegative { dialog -> dialog.dismiss() }
                            .setOnDismissListener { vm.setMessageShown(messageShow) }
                            .setCancelable(false)
                            .build(childFragmentManager)
                    }
                }
        }
    }

    fun setOnLoginSuccess(onLoginSuccess: () -> Unit): LoginDialog {
        this.onLoginSuccess = {
            onLoginSuccess()
            dismiss()
        }
        return this
    }

    fun show(fm: FragmentManager): LoginDialog {
        show(fm, this::class.java.simpleName)
        return this
    }
}