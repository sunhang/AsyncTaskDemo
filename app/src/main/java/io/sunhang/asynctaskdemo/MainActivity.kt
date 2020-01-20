package io.sunhang.asynctaskdemo

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import io.sunhang.asynctaskdemo.coroutines.CoroutineViewModel
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUI().setContentView(this)

        val model = ViewModelProviders.of(this).get(CoroutineViewModel::class.java)
        model.requestServer()
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
            fun panel(setup: _FrameLayout.() -> Unit) =
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

            verticalLayout {
                listOf(ID_LAYOUT_0, ID_LAYOUT_1, ID_LAYOUT_2).forEach {
                    panel {
                        id = it
                    }.lparams(matchParent, wrapContent) {
                        weight = 1.0f
                    }
                }
            }

        }.view
    }

}
