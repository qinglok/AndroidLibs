package x.being.androidlibs.widget

import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.Utils
import x.being.androidlibs.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

fun AppCompatActivity.initToolbar(
    title: String = getString(R.string.app_name),
    subtitle: String? = null,
    backEnable: Boolean = false
) {
    this.findViewById<Toolbar>(R.id.toolbar)?.apply {
        setSupportActionBar(this)

        this.title = title
        subtitle?.let {
            this.subtitle = subtitle
        }
        if (backEnable) {
            this.navigationIcon =
                ContextCompat.getDrawable(Utils.getApp(), R.drawable.ic_arrow_back_black_24dp)
            this.setNavigationOnClickListener {
                onBackPressed()
            }
        } else {
            this.navigationIcon = null
        }
    }
}

/**
 * 显示软键盘
 */
fun View.showSoftInput() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    postDelayed({
        requestFocus()
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }, 200)
}

/**
 * 切换软键盘
 */
fun View.showOrHideSoftInput() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    postDelayed({
        requestFocus()
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }, 200)
}

/**
 * 隐藏软键盘
 */
fun View.hideSoftInput() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

private val safeClickMap = WeakHashMap<Any ,Any>()

fun safeClick(tag : Any, action : () -> Unit){
    synchronized(safeClickMap){
        if (!safeClickMap.containsKey(tag)){
            safeClickMap[tag] = action
            action()
            GlobalScope.launch {
                delay(400)
                safeClickMap.remove(tag)
            }
        }
    }
}

fun View.safeClick(action : () -> Unit){
    this.setOnClickListener {
        safeClick(this, action)
    }
}

fun ViewPager.setViewPagerTime(time : Int = 500){
    try {
        val field= ViewPager::class.java.getDeclaredField("mScroller")
        field.isAccessible = true
        val scroller=  MsScroller(context,  AccelerateInterpolator())
        field.set(this, scroller)
        scroller.setmDuration(time)
    } catch ( e : Exception) {
        e.printStackTrace()
    }
}