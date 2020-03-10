package x.being.lib_net

import okhttp3.MediaType
import okio.Okio
import okio.ForwardingSink
import okio.BufferedSink
import okhttp3.RequestBody
import okio.Buffer
import java.io.IOException


internal class ExRequestBody(
    // 被代理的对象
    private val mOriginalBody: RequestBody,
    private val progressListener: ProgressListener
) : RequestBody() {
    // 总的长度
//    private var mTotalLength: Long = -1
    // 当前上传的长度
//    private var mCurrentLength: Long = -1

    override fun contentType(): MediaType? {
        return mOriginalBody.contentType()
    }

    override fun contentLength(): Long {
        return mOriginalBody.contentLength()
    }

    override fun writeTo(sink: BufferedSink) {
        var curSink = sink
        // Log.e("TAG", "进来了");
        // 获取总的长度
//        mTotalLength = contentLength()

        // 这里又是一个代理模式
        val forwardingSink = object : ForwardingSink(curSink) {
            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                // 写了多少数据叠加起来
//                mCurrentLength += byteCount
                // 我们要自己写一个接口回调出去
                progressListener.onProgress(byteCount)
            }
        }
        curSink = Okio.buffer(forwardingSink)
        // 最终调用者还是被代理对象的方法
        mOriginalBody.writeTo(curSink)
        // 一定要刷新，之前说过的连接池，复用等等
        curSink.flush()
    }

    internal interface ProgressListener {
        fun onProgress(current: Long)
    }
}