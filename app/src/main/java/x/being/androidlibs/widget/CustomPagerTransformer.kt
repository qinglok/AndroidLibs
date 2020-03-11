package x.being.androidlibs.widget

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.LogUtils

class CustomPagerTransformer :
    ViewPager.PageTransformer {
    private var viewPager: ViewPager? = null

    override fun transformPage(
        view: View,
        position: Float
    ) {
        if (viewPager == null) {
            viewPager = view.parent as ViewPager
        }

        val scrollX = viewPager!!.scrollX
        val maxScrollX = viewPager!!.measuredWidth
        val scale  = if (scrollX > maxScrollX / 2) {
            maxScrollX - scrollX.toFloat()
        } else {
            scrollX.toFloat()
        }
        var f = 1 - scale / maxScrollX
        if(f < 0.9) f = 0.9f
        LogUtils.d(scale, f)
        if (f > 0) {
            view.scaleX = f
            view.scaleY = f
        }
    }

}