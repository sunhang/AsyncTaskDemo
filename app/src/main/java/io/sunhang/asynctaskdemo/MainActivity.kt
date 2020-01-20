package io.sunhang.asynctaskdemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import io.sunhang.asynctaskdemo.coroutines.CoroutineViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.viewPager


class MainActivity : AppCompatActivity() {
    companion object {
        val ID_TOOLBAR = View.generateViewId()
        val ID_VIEW_PAGER = View.generateViewId()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUI().setContentView(this)

        val toolbar = findOptional<Toolbar>(ID_TOOLBAR)
        setSupportActionBar(toolbar)

        val model = ViewModelProviders.of(this).get(CoroutineViewModel::class.java)
        model.requestServer()
    }

    inner class ActivityUI : AnkoComponent<MainActivity> {

        override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {
            coordinatorLayout {
                val viewPager = viewPager {
                    id = ID_VIEW_PAGER
                    adapter = MainPagerAdapter(supportFragmentManager)
                }.lparams(matchParent, matchParent) {
                    behavior = AppBarLayout.ScrollingViewBehavior()
                }

                appBarLayout {
                    lparams(matchParent, wrapContent)

                    toolbar {
                        id = ID_TOOLBAR
                        popupTheme = R.style.Base_ThemeOverlay_AppCompat_Light
                        title = "异步Demo"
                    }.lparams(matchParent, context.dp2Px(56)) {
                        scrollFlags = (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                                or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
                    }

                    tabLayout {
                        setupWithViewPager(viewPager)
                        lparams(matchParent, context.dp2Px(48))
                    }
                }

            }
        }.view
    }

    class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return MainListFragment()
        }

        override fun getPageTitle(position: Int) = when(position) {
            0 -> "所有课程"
            1 -> "推荐"
            2 -> "我的信息"
            else -> null
        }

        override fun getCount() = 3

    }

}
