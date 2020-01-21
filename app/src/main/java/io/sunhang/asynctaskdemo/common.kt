package io.sunhang.asynctaskdemo

import android.content.Context
import io.sunhang.asynctaskdemo.model.Resource
import kotlin.math.roundToInt

fun Context.dp2Px(dp: Float) = (resources.displayMetrics.density * dp).roundToInt()
fun Context.dp2Px(dp: Int) = (resources.displayMetrics.density * dp).roundToInt()


fun threadInfo() = "[thread: ${Thread.currentThread().toString()}]"
