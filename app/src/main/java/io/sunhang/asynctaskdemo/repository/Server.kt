package io.sunhang.asynctaskdemo.repository

import io.sunhang.asynctaskdemo.model.Goods
import java.util.*

/**
 * 模拟请求服务器
 */
class Server {
    fun getGoodsFromShopA(): Goods {
        Thread.sleep((Random().nextFloat() * 4000).toLong())
        return Goods("桌子A", 100.0f, 60.0f)
    }

    fun getGoodsFromShopB(): Goods {
        Thread.sleep((Random().nextFloat() * 4000).toLong())
        return Goods("桌子B", 75.0f, 49.0f)
    }
}