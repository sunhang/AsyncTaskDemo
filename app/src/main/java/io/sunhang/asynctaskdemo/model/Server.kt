package io.sunhang.asynctaskdemo.model

import io.sunhang.asynctaskdemo.model.Goods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.*

/**
 * 模拟请求服务器
 */
class Server {
    /**
     * 从宜家买桌子
     */
    fun getGoodsFromIKEA(): Goods{
        Thread.sleep((Random().nextFloat() * 4000).toLong())
        return Goods("桌子A", 100.0f, 60.0f)
    }

    /**
     * 从家乐福买桌子
     */
    fun getGoodsFromCarrefour(): Goods{
        Thread.sleep((Random().nextFloat() * 4000).toLong())
        return Goods("桌子B", 75.0f, 49.0f)
    }
}