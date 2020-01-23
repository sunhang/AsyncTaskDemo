package io.sunhang.asynctaskdemo

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

typealias HandlerClick = suspend CoroutineScope.(v: android.view.View?) -> Unit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            gravity = Gravity.CENTER

            myButton {
                category = "primitive"
            }

            myButton {
                category = "coroutines"
            }

            myButton {
                category = "rxjava"
            }

            myButton {
                category = "thread-pool"
            }

            myButton {
                category = "complete-future"
                onClick {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        startActivity<GoodsActivity>("async_impl_type" to "complete-future")
                    } else {
                        toast("not supported")
                    }
                }
            }
        }
    }

    fun _LinearLayout.myButton(init: Config.() -> Unit) {
        val config = Config().apply(init)

        val clickHandler: HandlerClick = config.onClick.selfIfNull {
            {
                startActivity<GoodsActivity>("async_impl_type" to config.category)
            }
        }

        button {
            text = config.category
            isAllCaps = false
            onClick(handler = clickHandler)
        }
    }

    class Config {
        lateinit var category: String
        var onClick: HandlerClick? = null
    }

}