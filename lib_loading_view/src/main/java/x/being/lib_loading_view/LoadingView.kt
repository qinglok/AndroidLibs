package x.being.lib_loading_view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class LoadingView : DialogFragment() {

    companion object{
        private const val key_text = "_TEXT"
        fun create(text : String):LoadingView{
            val fragment = LoadingView()
            fragment.arguments = Bundle().apply {
                putString(key_text, text)
            }
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // 禁用点击空白
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        // 禁用返回键
        dialog.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading_dialog, container, false).also {
            // 对话框内部的背景设为透明
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.findViewById<TextView>(R.id.tv).text = arguments?.getString(key_text)
        }
    }
}