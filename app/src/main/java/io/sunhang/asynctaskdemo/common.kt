package io.sunhang.asynctaskdemo

import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.sunhang.asynctaskdemo.model.Resource
import kotlin.math.roundToInt

fun Context.dp2Px(dp: Float) = (resources.displayMetrics.density * dp).roundToInt()
fun Context.dp2Px(dp: Int) = (resources.displayMetrics.density * dp).roundToInt()

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

fun threadInfo() = "[thread: ${Thread.currentThread().toString()}]"

fun Any?.isNull() = (this == null)

fun <T> T?.selfIfNull(instantiate: ()->T): T {
    return if (this.isNull()) {
        instantiate()
    } else this!!
}