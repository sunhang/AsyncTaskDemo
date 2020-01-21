package io.sunhang.asynctaskdemo.coroutines

import io.sunhang.asynctaskdemo.model.BackendWork
import io.sunhang.asynctaskdemo.model.Goods
import kotlinx.coroutines.*


class GoodsModel {
    private val server = BackendWork()

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

    /**
     * 比较出更好的桌子
     */
    fun selectBetterOneAsync(
        supervisorJob: Job,
        ikeaGoods: Goods,
        carrefourGoods: Goods
    ): Deferred<Goods> {
        return CoroutineScope(supervisorJob + newSingleThreadContext("foo")).async {
            server.selectBetterOne(ikeaGoods, carrefourGoods)
        }
    }
}