package io.sunhang.asynctaskdemo

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.sunhang.asynctaskdemo.coroutines.CoroutineViewModel
import io.sunhang.asynctaskdemo.model.Goods
import kotlinx.coroutines.*
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity() {
    private val supervisorJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + supervisorJob)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUI().setContentView(this)

        val model = ViewModelProviders.of(this).get(CoroutineViewModel::class.java)
        model.requestServer()

        uiScope.launch {
            displayGoods(ActivityUI.ID_LAYOUT_0, model.goodsA.await())
        }
        uiScope.launch {
            displayGoods(ActivityUI.ID_LAYOUT_1, model.goodsB.await())
        }
        uiScope.launch {
            val layoutId = ActivityUI.ID_LAYOUT_2
            val betterGoods = model.betterGoods.await()
            textView(layoutId).text = "choose: \n$betterGoods"
            progressBar(layoutId).visibility = View.INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
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
            val ID_TEXT_VIEW = View.generateViewId()
            val ID_PROGRESS = View.generateViewId()

            val ID_LAYOUT_0 = View.generateViewId()
            val ID_LAYOUT_1 = View.generateViewId()
            val ID_LAYOUT_2 = View.generateViewId()
        }


        override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {
            fun _LinearLayout.panel(setup: _FrameLayout.() -> Unit) = run {
                frameLayout {
                    setup()

                    textView {
                        id = ID_TEXT_VIEW
                        textSize = 20f
                    }.lparams(wrapContent, wrapContent) {
                        gravity = Gravity.CENTER
                    }

                    progressBar {
                        id = ID_PROGRESS
                    }.lparams(wrapContent, wrapContent) {
                        gravity = Gravity.CENTER
                    }
                }
            }

            verticalLayout {
                listOf(ID_LAYOUT_0, ID_LAYOUT_1, ID_LAYOUT_2).forEach {
                    panel {
                        id = it
                    }.lparams(matchParent, matchParent) {
                        weight = 1.0f
                    }
                }
            }

        }.view
    }

}
