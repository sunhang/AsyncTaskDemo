package io.sunhang.asynctaskdemo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.marginTop
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import io.sunhang.asynctaskdemo.coroutines.GoodsView
import io.sunhang.asynctaskdemo.coroutines.GoodsPresenter
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.model.Resource
import org.jetbrains.anko.*


class MainActivity : MvpActivity<GoodsView, GoodsPresenter>(), GoodsView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun createPresenter(): GoodsPresenter {
        return GoodsPresenter()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.cancel()
    }

    private fun displayGoods(layoutId: Int, resource: Resource) {
        textView(layoutId).text = when (resource.status) {
            Resource.START -> resource.any as String
            Resource.FINISH -> (resource.any as Goods).toString()
            else -> ""
        }

        if (resource.status == Resource.FINISH) {
            progressBar(layoutId).visibility = View.INVISIBLE
            findOptional<ViewGroup>(layoutId)!!.background = ColorDrawable(Color.YELLOW)
        }
    }

    private fun textView(layoutId: Int): TextView {
        return findOptional<ViewGroup>(layoutId)!!.findOptional<TextView>(ActivityUI.ID_TEXT_VIEW)!!
    }

    private fun progressBar(layoutId: Int): ProgressBar {
        return findOptional<ViewGroup>(layoutId)!!.findOptional<ProgressBar>(ActivityUI.ID_PROGRESS)!!
    }


    class ActivityUI : AnkoComponent<MainActivity> {
        companion object {
            val ID_TITLE = View.generateViewId()
            val ID_TEXT_VIEW = View.generateViewId()
            val ID_PROGRESS = View.generateViewId()

            val ID_LAYOUT_0 = View.generateViewId()
            val ID_LAYOUT_1 = View.generateViewId()
            val ID_LAYOUT_2 = View.generateViewId()
        }

        override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {

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

            relativeLayout {
                id = config.id

                val vl = verticalLayout {
                    id = View.generateViewId()

                    textView {
                        id = ID_TITLE
                        textSize = 23f
                        text = config.title
                        textColor = Color.BLACK
                    }

                    textView {
                        id = ID_TEXT_VIEW
                        textSize = 20f
                    }

                }.lparams(matchParent, wrapContent) {
                    leftMargin = context.dp2Px(20)
                    topMargin = context.dp2Px(60)
                    centerHorizontally()
                }


                progressBar {
                    id = ID_PROGRESS
                }.lparams(wrapContent, wrapContent) {
                    centerHorizontally()
                    below(vl)
                }
            }.lparams(matchParent, matchParent) {
                weight = 1.0f
            }
        }

    }
}
