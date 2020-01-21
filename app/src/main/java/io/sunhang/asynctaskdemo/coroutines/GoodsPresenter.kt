package io.sunhang.asynctaskdemo.coroutines

import androidx.annotation.WorkerThread
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.model.Resource
import kotlinx.coroutines.*
import java.util.*

class GoodsPresenter : MvpBasePresenter<GoodsView>() {
    private val supervisorJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    private val server = GoodsModel()

    fun requestServer() = uiScope.launch {
        val deferredIKEAGoods = server.getGoodsFromIKEAAsync()
        val deferredCarrefourGoods = server.getGoodsFromCarrefourAsync()

        view.displayIKEAGoods(Resource(Resource.LOADING, "start request IKEA goods"))
        view.displayCarrefourGoods(Resource(Resource.LOADING, "start request carrefour goods"))

        launch {
            val goods = deferredIKEAGoods.await()
            ifViewAttached {
                it.displayIKEAGoods(Resource(Resource.FINISH, goods))
            }
        }

        launch {
            val goods = deferredCarrefourGoods.await()
            ifViewAttached {
                it.displayCarrefourGoods(Resource(Resource.FINISH, goods))
            }
        }

        view.displayBetterGoods(Resource(Resource.LOADING, "wait..."))

        val ikeaGoods = deferredIKEAGoods.await()
        val carrefourGoods = deferredCarrefourGoods.await()

        ifViewAttached {
            it.displayBetterGoods(Resource(Resource.LOADING, "start compare which one is better"))
        }

        val betterGoods = withContext(supervisorJob + newSingleThreadContext("foo")) {
            selectBetterOne(ikeaGoods, carrefourGoods)
        }

        ifViewAttached {
            it.displayBetterGoods(Resource(Resource.FINISH, betterGoods))
        }
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