package io.sunhang.asynctaskdemo.coroutines

import io.sunhang.asynctaskdemo.model.Goods
import kotlinx.coroutines.*
import java.util.*

/**
 * 模拟请求服务器
 */
class Server {
    fun getGoodsFromShopA() = CoroutineScope(Dispatchers.IO).async {
        Thread.sleep((Random().nextFloat() * 4000).toLong())
        Goods("桌子A", 100.0f, 60.0f)
    }

    fun getGoodsFromShopB() = CoroutineScope(Dispatchers.IO).async {
        Thread.sleep((Random().nextFloat() * 4000).toLong())
        Goods("桌子B", 75.0f, 49.0f)
    }
}