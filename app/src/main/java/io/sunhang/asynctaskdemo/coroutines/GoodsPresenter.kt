package io.sunhang.asynctaskdemo.coroutines

import androidx.annotation.WorkerThread
import io.sunhang.asynctaskdemo.BaseGoodsPresenter
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.model.Resource
import kotlinx.coroutines.*
import java.util.*

class GoodsPresenter : BaseGoodsPresenter() {
    private val supervisorJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    private val server = GoodsModel()

    override fun requestServer() {
        uiScope.launch {
            val deferredIKEAGoods = server.getGoodsFromIKEAAsync()
            val deferredCarrefourGoods = server.getGoodsFromCarrefourAsync()

            view.displayIKEAGoods(Resource(Resource.LOADING, "start request IKEA goods"))
            view.displayCarrefourGoods(Resource(Resource.LOADING, "start request carrefour goods"))

            launch {
                val goods = deferredIKEAGoods.await()
                view.displayIKEAGoods(Resource(Resource.FINISH, goods))
            }

            launch {
                val goods = deferredCarrefourGoods.await()
                view.displayCarrefourGoods(Resource(Resource.FINISH, goods))
            }

            view.displayBetterGoods(
                Resource(
                    Resource.LOADING,
                    "wait\n=====================\n===================="
                )
            )

            val ikeaGoods = deferredIKEAGoods.await()
            val carrefourGoods = deferredCarrefourGoods.await()

            view.displayBetterGoods(Resource(Resource.LOADING, "start compare which one is better"))

            val betterGoods = withContext(supervisorJob + newSingleThreadContext("foo")) {
                selectBetterOne(ikeaGoods, carrefourGoods)
            }

            view.displayBetterGoods(Resource(Resource.FINISH, betterGoods))
        }
    }

    override fun cancel() {
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