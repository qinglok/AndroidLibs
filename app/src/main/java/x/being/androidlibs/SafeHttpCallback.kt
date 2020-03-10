package x.being.androidlibs

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import x.being.lib_net.HttpCallback

fun <T> LifecycleOwner.safeHttpCallback(
    onStart: () -> Unit = {},
    onFinish: () -> Unit = {},
    onSuccess: (t: T?) -> Unit = {},
    onError: (t: Throwable?) -> Unit = {}
): HttpCallback<T> {

    val start = 1
    val finish = 2
    val success = 3
    val error = 4
    val error401 = 5
    val error403 = 6
    val loginTimeout = 7

    data class CallStatus<T>(
        var status: Int? = null,
        var msg: String? = null,
        var throwable: Throwable? = null,
        var data: T? = null
    )

    return object : HttpCallback<T> {

        private fun setStatus(status: CallStatus<T>) {
            if (this@safeHttpCallback.lifecycle.currentState == Lifecycle.State.RESUMED) {
                updateView(status)
            } else {
                object : LifecycleObserver {

                    @Suppress("unused")
                    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    fun onResume() {
                        updateView(status)
                        this@safeHttpCallback.lifecycle.removeObserver(this)
                    }
                }.also {
                    this@safeHttpCallback.lifecycle.addObserver(it)
                }
            }
        }

        private fun updateView(status: CallStatus<T>) {
            ui {
                when (status.status) {
                    start -> {
                        onStart()
                    }
                    finish -> {
                        onFinish()
                    }
                    success -> {
                        onSuccess(status.data)
                    }
                    error -> {
                        LogUtils.e(status.throwable)
                        LogUtils.file(status)
                        ToastUtils.showLong("no net")
                        onError(status.throwable)
                    }
                    error403 -> {
                        ToastUtils.showLong(status.msg)
                    }
                    error401, loginTimeout -> {
                        showLoginTimeoutDialog()
                    }
                }
            }
        }

        private fun showLoginTimeoutDialog() {
            if(alertDialog == null){
                alertDialog = MaterialAlertDialogBuilder(ActivityUtils.getTopActivity()).setTitle("系统提示")
                    .setMessage("登录超时，请重新登录！")
                    .setPositiveButton("确定") { dialog, _ ->
                        dialog.dismiss()
//                        Auth.logOut()
//                        ActivityUtils.startActivity(LoginActivity::class.java)
                    }
                    .setCancelable(false)
                    .create()
                alertDialog!!.show()
            }else if(!alertDialog!!.isShowing){
                alertDialog!!.show()
            }
        }

        private var alertDialog : AlertDialog? = null

        override fun onStart() {
            setStatus(CallStatus(status = start))
        }

        override fun onFinish() {
            setStatus(CallStatus(status = finish))
        }

        override fun onSuccess(t: T?) {
            if (t is JsonResult<*>) {
                if (t.status == Status.UNAUTHORIZED) {
                    setStatus(CallStatus(status = loginTimeout))
                } else {
                    setStatus(CallStatus(status = success, data = t))
                }
            }
        }

        override fun onError(t: Throwable?) {
            setStatus(CallStatus(status = error, throwable = t))
        }


        override fun on401(msg: String) {
            setStatus(CallStatus(status = error401))
        }

        override fun on403(msg: String) {
            setStatus(CallStatus(status = error403))
        }
    }
}