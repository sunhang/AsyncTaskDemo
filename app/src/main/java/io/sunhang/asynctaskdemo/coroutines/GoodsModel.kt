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
    suspend fun selectBetterOneAsync(supervisorJob: Job, ikeaGoods: Goods, carrefourGoods: Goods): Goods {
        return withContext(supervisorJob + newSingleThreadContext("foo")) {
            server.selectBetterOne(ikeaGoods, carrefourGoods)
        }
    }
}