package io.sunhang.asynctaskdemo

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.button
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.verticalLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        class Config {
            lateinit var category: String
        }
        fun _LinearLayout.myButton(init: Config.()->Unit) {
            val config = Config().apply(init)

            button {
                text = config.category
                isAllCaps = false
                onClick {
                    startActivity<GoodsActivity>("async_impl_type" to config.category)
                }
            }
        }

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

            /*
            myButton {
                category = "thread-pool"
            }

            myButton {
                category = "complete-future"
            }*/
        }
    }
}