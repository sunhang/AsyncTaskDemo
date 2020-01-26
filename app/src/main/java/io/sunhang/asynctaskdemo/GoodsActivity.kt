package io.sunhang.asynctaskdemo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.model.Resource
import org.jetbrains.anko.*
import java.lang.RuntimeException


class GoodsActivity : MvpActivity<GoodsView, BaseGoodsPresenter>(),
    GoodsView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent?.extras?.get("async_impl_type") as? String
        title?.let {
            setTitle(it)
        }

        ActivityUI().setContentView(this)

        presenter.requestServer()
    }

    override fun displayIKEAGoods(resource: Resource) {
        when (resource.status) {
            Resource.LOADING -> textView(ActivityUI.ID_LAYOUT_0).text = getString(R.string.start_request_ikea_goods)
            Resource.FINISH -> displayGoods(ActivityUI.ID_LAYOUT_0, resource.any as Goods)
        }
    }

    override fun displayCarrefourGoods(resource: Resource) {
        when (resource.status) {
            Resource.LOADING -> textView(ActivityUI.ID_LAYOUT_1).text = getString(R.string.start_request_carrefour_goods)
            Resource.FINISH -> displayGoods(ActivityUI.ID_LAYOUT_1, resource.any as Goods)
        }
    }

    override fun displayBetterGoods(resource: Resource) {
        when (resource.status) {
            Resource.WAITING -> textView(ActivityUI.ID_LAYOUT_2).text = getString(R.string.waiting)
            Resource.LOADING -> textView(ActivityUI.ID_LAYOUT_2).text = getString(R.string.start_compare_which_one_is_better)
            Resource.FINISH -> displayGoods(ActivityUI.ID_LAYOUT_2, resource.any as Goods)
        }
    }

    override fun createPresenter(): BaseGoodsPresenter {
        val value = intent?.extras?.get("async_impl_type") ?: throw RuntimeException("no value")

        val packageName = when(value) {
            "rxjava" -> "rx"
            "thread-pool" -> "threadpool"
            "complete-future" -> "completefuture"
            else -> value
        }

        val cls = Class.forName("io.sunhang.asynctaskdemo.$packageName.GoodsPresenter")
        return cls.newInstance() as BaseGoodsPresenter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.cancel()
    }

    private fun displayGoods(layoutId: Int, goods: Goods) {
        textView(layoutId).text = goods.toString()
        progressBar(layoutId).visibility = View.GONE
        findOptional<ViewGroup>(layoutId)!!.background = ColorDrawable(Color.YELLOW)
    }

    private fun textView(layoutId: Int): TextView {
        return findOptional<ViewGroup>(layoutId)!!.findOptional<TextView>(ActivityUI.ID_TEXT_VIEW)!!
    }

    private fun progressBar(layoutId: Int): ProgressBar {
        return findOptional<ViewGroup>(layoutId)!!.findOptional<ProgressBar>(ActivityUI.ID_PROGRESS)!!
    }


    class ActivityUI : AnkoComponent<GoodsActivity> {
        companion object {
            val ID_TITLE = View.generateViewId()
            val ID_TEXT_VIEW = View.generateViewId()
            val ID_PROGRESS = View.generateViewId()

            val ID_LAYOUT_0 = View.generateViewId()
            val ID_LAYOUT_1 = View.generateViewId()
            val ID_LAYOUT_2 = View.generateViewId()
        }

        override fun createView(ui: AnkoContext<GoodsActivity>) = ui.apply {

            verticalLayout {
                panel {
                    id = ID_LAYOUT_0
                    title = context.getString(R.string.goods_from_shop_iKEA)
                }

                panel {
                    id = ID_LAYOUT_1
                    title = context.getString(R.string.goods_from_shop_carrefour)
                }

                panel {
                    id = ID_LAYOUT_2
                    title = context.getString(R.string.choose_better_goods)
                }
            }
        }.view

        class Config {
            var id: Int = 0
            lateinit var title: String
        }

        fun _LinearLayout.panel(init: Config.() -> Unit) = run {
            val config = Config().apply { init() }

            verticalLayout {
                id = config.id

                leftPadding = context.dp2Px(20)
                topPadding = context.dp2Px(20)

                linearLayout {
                    gravity = Gravity.CENTER_VERTICAL

                    progressBar {
                        id = ID_PROGRESS
                    }

                    textView {
                        id = ID_TITLE
                        textSize = 23f
                        text = config.title
                        textColor = Color.BLACK
                    }
                }

                textView {
                    id = ID_TEXT_VIEW
                    textSize = 20f
                }

            }.lparams(matchParent, wrapContent) {
                weight = 1.0f
            }
        }
    }
}
