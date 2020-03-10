package x.being.lib_net

import okhttp3.*
import java.io.File
import java.net.URLConnection.getFileNameMap
import java.util.*


@Suppress("unused")
class RequestWrapper(val tag: Any, private var url: String, var isHttps: Boolean = false) {
    private val headers = arrayListOf<Pair<String, String>>()
    private val params = arrayListOf<Pair<String, Any?>>()
    private var progressListener: ProgressListener? = null
    private var multiProgressListener: MultiProgressListener? = null

    private var json: JsonObjectParam? = null
    private var jsonList: List<JsonObjectParam>? = null

    fun enableProgress(listener: ProgressListener): RequestWrapper {
        progressListener = listener
        return this
    }

    fun headers(vararg headers: Pair<String, String?>) = apply {
        headers.forEach {
            it.second?.let { second ->
                this.headers.add(Pair(it.first, second))
            }
        }
    }

    fun headers(map: Map<String, String?>) = apply {
        map.forEach {
            it.value?.let { value ->
                this.headers.add(Pair(it.key, value))
            }
        }
    }

    fun header(key: String, value: String) = apply {
        this.headers.add(Pair(key, value))
    }

    fun params(vararg params: Pair<String, Any?>) = apply {
        params.forEach {
            it.second?.let { second ->
                this.params.add(Pair(it.first, second))
            }
        }
    }

    fun params(map: Map<String, Any?>) = apply {
        map.forEach {
            it.value?.let { value ->
                this.params.add(Pair(it.key, value))
            }
        }
    }

    fun param(key: String, value: Any?) = apply {
        this.params.add(Pair(key, value))
    }

    fun param(json: JsonObjectParam) = apply {
        this.json = json
    }

    fun param(jsonList: List<JsonObjectParam>) = apply {
        this.jsonList = jsonList
    }

    fun param(jsonList: ArrayList<JsonObjectParam>) = apply {
        this.jsonList = jsonList
    }

    fun buildGetRequest(): Request =
        Request.Builder()
            .tag(tag)
            .buildHeader()
            .buildGetUrl()
            .build()

    fun buildPostRequest(): Request =
        Request.Builder()
            .tag(tag)
            .buildHeader()
            .buildPostUrl()
            .buildBody()
            .build()

    private fun Request.Builder.buildHeader() = apply {
        headers.forEach {
            header(it.first, it.second)
        }
    }

    private fun Request.Builder.buildGetUrl() = apply {
        HttpUrl.parse(url)?.newBuilder()?.apply {
            params.forEach {
                addQueryParameter(it.first, it.second.toString())
            }
        }?.let {
            url(it.build())
        }
    }


    private fun Request.Builder.buildPostUrl() = apply {
        if (json == null) {
            HttpUrl.parse(url)?.let {
                url(it)
            }
        } else {
            HttpUrl.parse(url)?.newBuilder()?.apply {
                params.forEach {
                    addQueryParameter(it.first, it.second.toString())
                }
            }?.let {
                url(it.build())
            }
        }
    }

    private fun Request.Builder.buildBody() = apply {
        if (isMultiPart()) {
            MultipartBody.Builder().apply {
                setType(MultipartBody.FORM)
                params.forEach {
                    val key = it.first

                    when (val value = it.second) {
                        is File -> {
                            val identify = UUID.randomUUID().toString() + "." + value.extension
                            addFormDataPart(
                                key,
                                identify,
                                value.createRequestBody().let { requestBody ->
                                    if (progressListener != null) {
                                        ExRequestBody(
                                            requestBody,
                                            createProgressListener(requestBody)
                                        )
                                    } else {
                                        requestBody
                                    }
                                }
                            )
                        }
                        is MultiFile -> value.files.forEach { file ->
                            val identify = UUID.randomUUID().toString() + "." + file.extension
                            addFormDataPart(
                                key,
                                identify,
                                file.createRequestBody().let { requestBody ->
                                    if (progressListener != null) {
                                        ExRequestBody(
                                            requestBody,
                                            createProgressListener(requestBody)
                                        )
                                    } else {
                                        requestBody
                                    }
                                }
                            )
                        }
                        is MultiFilePath -> value.files.forEach { path ->
                            val file = File(path)
                            val identify = UUID.randomUUID().toString() + "." + file.extension
                            addFormDataPart(
                                key,
                                identify,
                                file.createRequestBody().let { requestBody ->
                                    if (progressListener != null) {
                                        ExRequestBody(
                                            requestBody,
                                            createProgressListener(requestBody)
                                        )
                                    } else {
                                        requestBody
                                    }
                                }
                            )
                        }
                        is JsonObjectParam -> {
                            addPart(value.createRequestBody(gson().toJson(value)))
                        }
                        else -> addFormDataPart(key, value.toString())
                    }
                }
            }.let {
                post(it.build())
            }
        } else if (json != null) {
            val requestBody = json!!.createRequestBody(gson().toJson(json))
            post(requestBody)
        } else if (jsonList != null) {
            val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson().toJson(jsonList))
            post(requestBody)
        } else {
            FormBody.Builder().apply {
                params.forEach { add(it.first, it.second.toString()) }
            }.let {
                post(it.build())
            }
        }
    }

    private fun createProgressListener(requestBody: RequestBody): MultiProgressListener {
        if (multiProgressListener == null) {
            multiProgressListener = MultiProgressListener(progressListener)
        }
        return multiProgressListener!!.apply {
            total += requestBody.contentLength()
        }
    }

    private fun isMultiPart() =
        params.any { it.second is File || it.second is MultiFile || it.second is MultiFilePath }

    fun File.createRequestBody(): RequestBody =
        RequestBody.create(MediaType.parse(mediaType()), this)

    fun File.mediaType() =
        getFileNameMap().getContentTypeFor(name)
            ?: when (extension.toLowerCase(Locale.getDefault())) {
                "jpg" -> "image/jpeg"
                "png" -> "image/png"
                "gif" -> "image/gif"
                "xml" -> "application/xml"
                "json" -> "application/json"
                "js" -> "application/javascript"
                "apk" -> "application/vnd.android.package-archive"
                "md" -> "text/x-markdown"
                "webp" -> "image/webp"
                else -> "application/octet-stream"
            }

    fun JsonObjectParam.createRequestBody(json: String): RequestBody =
        RequestBody.create(mediaType(), json)


    fun JsonObjectParam.mediaType() = MediaType.parse("application/json; charset=utf-8")

    data class MultiFile(
        var files: List<File>
    )

    data class MultiFilePath(
        var files: List<String>
    )

    internal class MultiProgressListener(
        private val progressListener: ProgressListener?
    ) : ExRequestBody.ProgressListener {
        private var current = 0L
        var total = 0L

        override fun onProgress(current: Long) {
            this.current += current
            progressListener?.onProgress(this.current, total)
        }

    }

    interface JsonObjectParam

    interface ProgressListener {
        fun onProgress(current: Long, total: Long)
    }

}


