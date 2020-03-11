package x.being.androidlibs.widget

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT 表示当前fragment会执行到onResume，其他fragment只会执行到onStart
 */
class TabsPagerAdapter(manager: FragmentManager, private val fragments: List<Fragment>) :
    FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}
