package io.sunhang.asynctaskdemo.coroutines

import io.sunhang.asynctaskdemo.model.Goods
import kotlinx.coroutines.*
import java.util.*

/**
 * 模拟请求服务器
 */
class GoodsModel {
    /**
     * 从宜家买桌子
     */
    fun getGoodsFromIKEAAsync() = CoroutineScope(Dispatchers.IO).async {
        Thread.sleep((Random().nextFloat() * 4000).toLong())
        Goods("桌子A", 100.0f, 60.0f)
    }

    /**
     * 从家乐福买桌子
     */
    fun getGoodsFromCarrefourAsync() = CoroutineScope(Dispatchers.IO).async {
        Thread.sleep((Random().nextFloat() * 4000).toLong())
        Goods("桌子B", 75.0f, 49.0f)
    }
}