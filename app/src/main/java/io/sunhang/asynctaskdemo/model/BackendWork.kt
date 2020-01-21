package io.sunhang.asynctaskdemo.model

import java.util.*

/**
 * 模拟请求服务器，以及后台耗时操作
 */
class BackendWork {
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

    /**
     * 模拟选择最好的桌子
     */
    fun selectBetterOne(vararg goods: Goods): Goods {
        // 模拟耗时操作
        Thread.sleep((Random().nextFloat() * 4000).toLong())
        return goods.maxBy {
            // 价格和质量，权重。质量越高越好，价格越低越好
            it.quality * 0.8f - it.price * 0.6f
        }!!
    }
}