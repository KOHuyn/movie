package com.kohuyn.movie.ui.alert

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.kohuyn.movie.R
import com.kohuyn.movie.databinding.DialogMessageBinding

class MessageDialog private constructor() : DialogFragment() {
    private lateinit var binding: DialogMessageBinding

    private var title: String? = null
    private var message: String? = null
    private var labelButtonNegative: String? = null
    private var labelButtonPositive: String? = null
    private var actionButtonNegative: ((dialog: MessageDialog) -> Unit)? = null
    private var actionButtonPositive: ((dialog: MessageDialog) -> Unit)? = null
    private var onDismissListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(activity)
        root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        val dialog = Dialog(activity as FragmentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMessageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = title
        binding.tvTitle.isGone = title.isNullOrBlank()

        binding.tvMessage.text = message
        binding.tvMessage.isGone = message.isNullOrBlank()

        binding.tvNegative.text = labelButtonNegative ?: getString(R.string.cancel)
        binding.tvNegative.setOnClickListener { actionButtonNegative?.invoke(this) }
        binding.tvNegative.isGone = actionButtonNegative == null

        binding.tvPositive.text = labelButtonPositive ?: getString(R.string.ok)
        binding.tvPositive.setOnClickListener { actionButtonPositive?.invoke(this) }
        binding.tvPositive.isGone = actionButtonPositive == null
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListener?.invoke()
        super.onDismiss(dialog)
    }

    class Builder {
        private val dialog: MessageDialog = MessageDialog()
        fun setTitle(title: String?): Builder {
            dialog.title = title
            return this
        }

        fun setMessage(message: String?): Builder {
            dialog.message = message
            return this
        }

        fun setButtonNegative(
            label: String? = null,
            action: (dialog: MessageDialog) -> Unit
        ): Builder {
            dialog.labelButtonNegative = label
            dialog.actionButtonNegative = action
            return this
        }

        fun setButtonPositive(
            label: String? = null,
            action: (dialog: MessageDialog) -> Unit
        ): Builder {
            dialog.labelButtonPositive = label
            dialog.actionButtonPositive = action
            return this
        }

        fun setOnDismissListener(onDismissListener: () -> Unit): Builder {
            dialog.onDismissListener = onDismissListener
            return this
        }

        fun setCancelable(isCancelable: Boolean): Builder {
            val isNonButtonVisible =
                dialog.actionButtonNegative == null && dialog.actionButtonPositive == null
            dialog.isCancelable = isCancelable || isNonButtonVisible
            return this
        }

        fun build(fm: FragmentManager): MessageDialog {
            dialog.show(fm)
            return dialog
        }
    }

    fun show(fm: FragmentManager) {
        show(fm, this::class.java.simpleName)
    }
}