package io.sunhang.asynctaskdemo.coroutines

import io.sunhang.asynctaskdemo.BaseGoodsPresenter
import io.sunhang.asynctaskdemo.model.Resource
import kotlinx.coroutines.*

class GoodsPresenter : BaseGoodsPresenter() {
    private val supervisorJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + supervisorJob)

    private val goodsModel = GoodsModel()

    override fun requestServer() {
        uiScope.launch {
            val deferredIKEAGoods = goodsModel.getGoodsFromIKEAAsync()
            val deferredCarrefourGoods = goodsModel.getGoodsFromCarrefourAsync()

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

            val strWaiting = "wait\n=====================\n===================="
            view.displayBetterGoods(Resource(Resource.LOADING, strWaiting))

            val ikeaGoods = deferredIKEAGoods.await()
            val carrefourGoods = deferredCarrefourGoods.await()

            view.displayBetterGoods(Resource(Resource.LOADING, "start compare which one is better"))

            val betterGoods =
                goodsModel.selectBetterOneAsync(supervisorJob, ikeaGoods, carrefourGoods)
            view.displayBetterGoods(Resource(Resource.FINISH, betterGoods))
        }
    }

    override fun cancel() {
        supervisorJob.cancel()
    }
}