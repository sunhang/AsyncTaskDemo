package io.sunhang.asynctaskdemo.model

import androidx.annotation.FloatRange

data class Goods(
    val name: String,
    @FloatRange(from = 0.0) val price: Float, // 价格
    @FloatRange(from = 0.0, to = 100.0) val quality: Float // 质量
) {
    override fun toString(): String {
        return "$name >> price:$price quality:$quality"
    }
}
