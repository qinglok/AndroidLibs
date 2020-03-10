@file:Suppress("unused")

package x.being.lib_net

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


fun String.http(tag: Any = this) = RequestWrapper(tag, this)

fun String.https(tag: Any = this) = RequestWrapper(tag, this, true)

inline fun <reified T> RequestWrapper.get(httpCallback: HttpCallback<T>) =
    call(1, httpCallback)

inline fun <reified T> RequestWrapper.post(httpCallback: HttpCallback<T>) =
    call(2, httpCallback)

inline fun <reified T> RequestWrapper.call(
    httpMethod: Int,
    httpCallback: HttpCallback<T>
) {

    httpCallback.onStart()

    val request = if (httpMethod == 1)
        buildGetRequest()
    else
        buildPostRequest()

    val client = if (isHttps) HttpWrapper.okHttpsClient else HttpWrapper.okHttpClient
    val call = client.newCall(request)
    HttpWrapper.requestCache[tag] = call

    call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            httpCallback.onFinish()
            httpCallback.onError(e)
        }

        override fun onResponse(call: Call, response: Response) {
            httpCallback.onFinish()

            if (response.isSuccessful) {
                try {
                    val data = gson().fromJson<T>(response.body()?.charStream(), object : TypeToken<T>() {}.type)
                    response.body()?.close()
                    httpCallback.onSuccess(data)
                } catch (e: Throwable) {
                    httpCallback.onError(e)
                }
            } else {
                when {
                    response.code() == 401 -> httpCallback.on401(response.body()?.string() ?: "")
                    response.code() == 403 -> httpCallback.on403(response.body()?.string() ?: "")
                    else -> httpCallback.onError(IOException("request to ${request.url()} is fail; http code: ${response.code()}!"))
                }
            }
        }
    })
}

fun gson() = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()