package io.sunhang.asynctaskdemo.primitive

import android.os.Handler
import android.os.Looper
import io.sunhang.asynctaskdemo.BaseGoodsPresenter
import io.sunhang.asynctaskdemo.model.BackendWork
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.model.Resource
import sunhang.openlibrary.guardLet

class GoodsPresenter : BaseGoodsPresenter() {
    private val server = BackendWork()
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var canceled = false

    override fun requestServer() {
        view.displayIKEAGoods(Resource(Resource.LOADING, "start request IKEA goods"))
        view.displayCarrefourGoods(Resource(Resource.LOADING, "start request carrefour goods"))
        val strWaiting = "wait\n=====================\n====================="
        view.displayBetterGoods(Resource(Resource.LOADING, strWaiting))

        var ikeaGoods: Goods? = null
        var carrefourGoods: Goods? = null

        Thread {
            try {
                val goods = server.getGoodsFromIKEA()

                mainThreadHandler.post {
                    if (canceled) return@post

                    ikeaGoods = goods
                    view.displayIKEAGoods(Resource(Resource.FINISH, goods))

                    val (ikeaGoods, carrefourGoods) = guardLet(
                        ikeaGoods,
                        carrefourGoods
                    ) { return@post }
                    betterGoods(ikeaGoods, carrefourGoods)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()

        Thread {
            try {
                val goods = server.getGoodsFromCarrefour()
                mainThreadHandler.post {
                    if (canceled) return@post

                    carrefourGoods = goods
                    view.displayCarrefourGoods(Resource(Resource.FINISH, goods))

                    val (ikeaGoods, carrefourGoods) = guardLet(
                        ikeaGoods,
                        carrefourGoods
                    ) { return@post }
                    betterGoods(ikeaGoods, carrefourGoods)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun betterGoods(ikeaGoods: Goods, carrefourGoods: Goods) {
        view.displayBetterGoods(Resource(Resource.LOADING, "start compare which one is better"))

        Thread {
            try {
                val goods = server.selectBetterOne(ikeaGoods, carrefourGoods)

                mainThreadHandler.post {
                    if (canceled) return@post

                    view.displayBetterGoods(Resource(Resource.FINISH, goods))
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }

    override fun cancel() {
        canceled = true
    }

}