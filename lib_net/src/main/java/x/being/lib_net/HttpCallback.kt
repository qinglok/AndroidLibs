package x.being.lib_net

interface HttpCallback<T> {
    fun onStart()

    fun onFinish()

    fun onSuccess(t: T?)

    fun onError(t: Throwable?)

    fun on401(msg: String)

    fun on403(msg: String)
}