package io.sunhang.asynctaskdemo.primitive

import android.os.Handler
import android.os.Looper
import io.sunhang.asynctaskdemo.BaseGoodsPresenter
import io.sunhang.asynctaskdemo.model.BackendWork
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.model.Resource
import sunhang.openlibrary.safeLet

class GoodsPresenter : BaseGoodsPresenter() {
    private val backendWork = BackendWork()
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var canceled = false

    private val threads = mutableSetOf<Thread>()

    override fun requestServer() {
        view.displayIKEAGoods(Resource(Resource.LOADING))
        view.displayCarrefourGoods(Resource(Resource.LOADING))
        view.displayBetterGoods(Resource(Resource.WAITING))

        var ikeaGoods: Goods? = null
        var carrefourGoods: Goods? = null

        val uiTask = { action: () -> Unit ->
            mainThreadHandler.post {
                if (canceled) return@post

                action()
                safeLet(ikeaGoods, carrefourGoods) { it0, it1 ->
                    betterGoods(it0, it1)
                }
            }
        }

        threads += Thread {
            try {
                val goods = backendWork.getGoodsFromIKEA()
                uiTask {
                    ikeaGoods = goods
                    view.displayIKEAGoods(Resource(Resource.FINISH, goods))
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
                val goods = backendWork.getGoodsFromCarrefour()
                uiTask {
                    carrefourGoods = goods
                    view.displayCarrefourGoods(Resource(Resource.FINISH, goods))
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
        view.displayBetterGoods(Resource(Resource.LOADING))

        threads += Thread {
            try {
                val goods = backendWork.selectBetterOne(ikeaGoods, carrefourGoods)

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