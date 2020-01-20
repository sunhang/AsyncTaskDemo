package io.sunhang.asynctaskdemo.coroutines

import androidx.annotation.WorkerThread
import io.sunhang.asynctaskdemo.model.Goods
import kotlinx.coroutines.*
import java.util.*

class Presenter {
    lateinit var view: IView
    private val supervisorJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    private val server = Server()

    fun requestServer() = uiScope.launch {
        val goodsA = server.getGoodsFromShopA()
        val goodsB = server.getGoodsFromShopB()


        launch {
            view.displayGoodsA(goodsA.await())
        }

        launch {
            view.displayGoodsB(goodsB.await())
        }


        val betterGoods = withContext(supervisorJob + newSingleThreadContext("foo")) {
            selectBetterOne(goodsA.await(), goodsB.await())
        }

        view.displayBetterGoods(betterGoods)
    }

    fun cancel() {
        supervisorJob.cancel()
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