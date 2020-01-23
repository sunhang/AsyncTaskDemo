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

    private val threads = mutableSetOf<Thread>()

    private operator fun <T> MutableSet<T>.plusAssign(t: T) {
        add(t)
    }

    private operator fun <T> MutableSet<T>.minusAssign(t: T) {
        remove(t)
    }

    override fun requestServer() {
        view.displayIKEAGoods(Resource(Resource.LOADING, "start request IKEA goods"))
        view.displayCarrefourGoods(Resource(Resource.LOADING, "start request carrefour goods"))
        val strWaiting = "wait\n=====================\n====================="
        view.displayBetterGoods(Resource(Resource.LOADING, strWaiting))

        var ikeaGoods: Goods? = null
        var carrefourGoods: Goods? = null

        threads += Thread {
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
            } finally {
                val currentThread = Thread.currentThread()
                mainThreadHandler.post {
                    threads -= currentThread
                }
            }
        }.apply { start() }

        threads += Thread {
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
            } finally {
                val currentThread = Thread.currentThread()
                mainThreadHandler.post {
                    threads -= currentThread
                }
            }
        }.apply { start() }
    }

    private fun betterGoods(ikeaGoods: Goods, carrefourGoods: Goods) {
        view.displayBetterGoods(Resource(Resource.LOADING, "start compare which one is better"))

        threads += Thread {
            try {
                val goods = server.selectBetterOne(ikeaGoods, carrefourGoods)

                mainThreadHandler.post {
                    if (canceled) return@post

                    view.displayBetterGoods(Resource(Resource.FINISH, goods))
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                val currentThread = Thread.currentThread()
                mainThreadHandler.post {
                    threads -= currentThread
                }
            }
        }.apply { start() }
    }

    override fun cancel() {
        canceled = true
        threads.forEach { it.interrupt() }
    }

}