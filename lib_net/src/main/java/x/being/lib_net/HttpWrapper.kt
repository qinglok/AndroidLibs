package x.being.lib_net

import okhttp3.Call
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream
import java.lang.NullPointerException


@Suppress("unused")
object HttpWrapper {

    private var isAppDebug: Boolean = true
    private var connectTimeout: Long = 30000
    private var readTimeout: Long = 60000
    private var writeTimeout: Long = 60000

    private var cer: InputStream? = null

    fun init(isAppDebug: Boolean = true, connectTimeout: Long = 30000, readTimeout: Long = 60000, writeTimeout: Long = 60000, cer: InputStream? = null){
        HttpWrapper.isAppDebug = isAppDebug
        HttpWrapper.connectTimeout = connectTimeout
        HttpWrapper.readTimeout = readTimeout
        HttpWrapper.writeTimeout = writeTimeout
        HttpWrapper.cer = cer
    }

    val requestCache by lazy { hashMapOf<Any, Call>() }

    private val okHttpClientBuilder: OkHttpClient.Builder by lazy {
        OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            .apply {
                if (isAppDebug) {
                    val loggingInterceptor = HttpLoggingInterceptor().apply {
                        // 包含header、body数据
                        level = HttpLoggingInterceptor.Level.BODY
                    }

                    //http数据log，日志中打印出HTTP请求&响应数据
                    addInterceptor(loggingInterceptor)
                }
            }
    }

    val okHttpClient: OkHttpClient by lazy {
        okHttpClientBuilder.build()
    }

    val okHttpsClient: OkHttpClient by lazy {
        if (cer == null) {
            //未设置证书不能调用HTTPS
            throw NullPointerException("https cer can not be null!")
        }
        okHttpClientBuilder.apply {
            with(HttpsUtils.getSslSocketFactory(cer)) {
                sslSocketFactory(sSLSocketFactory, trustManager)
            }
        }.build()
    }

    fun cancel(tag: Any) {
        requestCache[tag]?.cancel()
        requestCache.remove(tag)
    }

}