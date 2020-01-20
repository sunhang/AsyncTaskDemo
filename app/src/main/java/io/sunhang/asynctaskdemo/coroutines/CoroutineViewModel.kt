package io.sunhang.asynctaskdemo.coroutines

import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.repository.Server
import kotlinx.coroutines.*
import java.util.*

class CoroutineViewModel : ViewModel() {
    val goodsA = MutableLiveData<Goods>()
    val goodsB = MutableLiveData<Goods>()
    val betterGoods = MutableLiveData<Goods>()

    private val server = Server()

    fun requestServer() = viewModelScope.launch {
        val deferredGoodsA = async(Dispatchers.IO) {
            server.getGoodsFromShopA()
        }
        val deferredGoodsB = async(Dispatchers.IO) {
            server.getGoodsFromShopB()
        }

        val notifyGoodsToUI = { deferredGoods: Deferred<Goods>, liveDataGoods: MutableLiveData<Goods> ->
            deferredGoods.run {
                invokeOnCompletion {
                    if (isCompleted) {
                        liveDataGoods.postValue(getCompleted())
                    }
                }
            }
        }

        notifyGoodsToUI(deferredGoodsA, goodsA)
        notifyGoodsToUI(deferredGoodsB, goodsB)

        val deferredBetterGoods = async(newSingleThreadContext("foo")) {
            selectBetterOne(deferredGoodsA.await(), deferredGoodsB.await())
        }
        notifyGoodsToUI.invoke(deferredBetterGoods, betterGoods)
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