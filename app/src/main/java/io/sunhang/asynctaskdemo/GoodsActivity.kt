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
        displayGoods(ActivityUI.ID_LAYOUT_0, resource)
    }

    override fun displayCarrefourGoods(resource: Resource) {
        displayGoods(ActivityUI.ID_LAYOUT_1, resource)
    }

    override fun displayBetterGoods(resource: Resource) {
        displayGoods(ActivityUI.ID_LAYOUT_2, resource)
    }

    override fun createPresenter(): BaseGoodsPresenter {
        return when (intent?.extras?.get("async_impl_type")) {
            "coroutines" -> io.sunhang.asynctaskdemo.coroutines.GoodsPresenter()
            "rxjava" -> io.sunhang.asynctaskdemo.rx.GoodsPresenter()
            "primitive" -> io.sunhang.asynctaskdemo.primitive.GoodsPresenter()
            "thread-pool" -> io.sunhang.asynctaskdemo.threadpool.GoodsPresenter()
            else -> throw RuntimeException("null")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.cancel()
    }

    private fun displayGoods(layoutId: Int, resource: Resource) {
        textView(layoutId).text = when (resource.status) {
            Resource.LOADING -> resource.any as String
            Resource.FINISH -> (resource.any as Goods).toString()
            else -> ""
        }

        if (resource.status == Resource.FINISH) {
            progressBar(layoutId).visibility = View.GONE
            findOptional<ViewGroup>(layoutId)!!.background = ColorDrawable(Color.YELLOW)
        }
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
                    title = "Goods from Shop IKEA:"
                }

                panel {
                    id = ID_LAYOUT_1
                    title = "Goods from Shop Carrefour:"
                }

                panel {
                    id = ID_LAYOUT_2
                    title = "Choose better goods:"
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
