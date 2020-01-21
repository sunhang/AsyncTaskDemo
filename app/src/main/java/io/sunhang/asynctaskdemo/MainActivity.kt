package io.sunhang.asynctaskdemo

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.button
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.verticalLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            gravity = Gravity.CENTER

            button {
                text = "coroutines"
                isAllCaps = false
                onClick {
                    startActivity<GoodsActivity>("async_impl_type" to "coroutines")
                }
            }

            button {
                text = "rxjava"
                isAllCaps = false
                onClick {
                    startActivity<GoodsActivity>("async_impl_type" to "rxjava")
                }
            }
        }
    }
}