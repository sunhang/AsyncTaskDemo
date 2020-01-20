package io.sunhang.asynctaskdemo.coroutines

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.repository.Server
import kotlinx.coroutines.*
import java.util.*

class CoroutineViewModel : ViewModel() {
    lateinit var goodsA: Deferred<Goods>
    lateinit var goodsB: Deferred<Goods>
    lateinit var betterGoods: Deferred<Goods>

    private val server = Server()

    fun requestServer() = viewModelScope.launch {
        goodsA = async(Dispatchers.IO) {
            server.getGoodsFromShopA()
        }
        goodsB= async(Dispatchers.IO) {
            server.getGoodsFromShopB()
        }

        betterGoods = async(newSingleThreadContext("foo")) {
            selectBetterOne(goodsA.await(), goodsB.await())
        }
    }

    @WorkerThread
    private fun selectBetterOne(vararg goods: Goods): Goods {
        // 模拟耗时操作
        Thread.sleep((Random().nextFloat() * 4000).toLong())
        return goods.maxBy {
            // 价格和质量，权重。质量越高越好，价格越低越好
            it.quality * 0.8f - it.price * 0.6f
        }!!
    }
}