package x.being.androidlibs

import android.os.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val handler = Handler()

fun bg(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(block = block)

fun ui(block: () -> Unit) = handler.post(block)

fun ui(delayed: Long, block: () -> Unit) = handler.postDelayed(block, delayed)