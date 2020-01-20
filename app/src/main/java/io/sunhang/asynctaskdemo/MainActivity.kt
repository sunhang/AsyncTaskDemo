package io.sunhang.asynctaskdemo

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.sunhang.asynctaskdemo.coroutines.IView
import io.sunhang.asynctaskdemo.coroutines.Presenter
import io.sunhang.asynctaskdemo.model.Goods
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity(), IView {
    private val presenter = Presenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUI().setContentView(this)

        presenter.view = this
        presenter.requestServer()
    }

    override fun displayGoodsA(goods: Goods) {
        displayGoods(ActivityUI.ID_LAYOUT_0, goods)
    }

    override fun displayGoodsB(goods: Goods) {
        displayGoods(ActivityUI.ID_LAYOUT_1, goods)
    }

    override fun displayBetterGoods(goods: Goods) {
        displayGoods(ActivityUI.ID_LAYOUT_2, goods)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.cancel()
    }

    private fun displayGoods(layoutId: Int, goods: Goods) {
        textView(layoutId).text = goods.toString()
        progressBar(layoutId).visibility = View.INVISIBLE
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
                    title = "Goods from Shop A:"
                }

                panel {
                    id = ID_LAYOUT_1
                    title = "Goods from Shop B:"
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
                    }

                    textView {
                        id = ID_TEXT_VIEW
                        textSize = 20f
                    }
                }.lparams(matchParent, wrapContent) {
                    leftMargin = context.dp2Px(20)
                    centerInParent()
                }


                progressBar {
                    id = ID_PROGRESS
                }.lparams(wrapContent, wrapContent) {
                    centerInParent()
                    below(vl)
                }
            }.lparams(matchParent, matchParent) {
                weight = 1.0f
            }
        }

    }
}
