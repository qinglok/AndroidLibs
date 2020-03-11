package x.being.androidlibs.widget

import android.app.Activity
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Fragment.msg(msg : String, onDismiss : (DialogInterface) -> Unit = {}){
    MaterialAlertDialogBuilder(activity)
            .setTitle("提示")
            .setMessage(msg)
            .setPositiveButton("好的", null)
            .setOnDismissListener(onDismiss)
            .show()
}

fun Activity.msg(msg : String, onDismiss : (DialogInterface) -> Unit = {}){
    MaterialAlertDialogBuilder(this)
            .setTitle("提示")
            .setMessage(msg)
            .setPositiveButton("好的", null)
            .setOnDismissListener(onDismiss)
            .show()
}
