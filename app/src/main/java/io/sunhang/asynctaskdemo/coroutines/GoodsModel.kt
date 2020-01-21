package io.sunhang.asynctaskdemo.coroutines

import io.sunhang.asynctaskdemo.model.Server
import kotlinx.coroutines.*


class GoodsModel {
    private val server = Server()

    /**
     * 从宜家买桌子
     */
    fun getGoodsFromIKEAAsync() = CoroutineScope(Dispatchers.IO).async {
        server.getGoodsFromIKEA()
    }

    /**
     * 从家乐福买桌子
     */
    fun getGoodsFromCarrefourAsync() = CoroutineScope(Dispatchers.IO).async {
        server.getGoodsFromCarrefour()
    }
}