package io.sunhang.asynctaskdemo.threadpool

import android.os.Handler
import android.os.Looper
import io.sunhang.asynctaskdemo.BaseGoodsPresenter
import io.sunhang.asynctaskdemo.model.BackendWork
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.model.Resource
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.Future

class GoodsPresenter : BaseGoodsPresenter() {
    private val backendWork = BackendWork()
    private val backendExecutor = Executors.newCachedThreadPool()
    private val futures = mutableSetOf<WeakReference<Future<Goods>>>()
    private val mainHandler = Handler(Looper.getMainLooper())

    private fun Future<Goods>.addToSet(): Future<Goods> {
        futures += WeakReference<Future<Goods>>(this)
        return this
    }

    private fun Goods.postToMainThread(task: (Goods) -> Unit): Goods {
        mainHandler.post {
            task(this)
        }

        return this
    }

    override fun requestServer() {
        /**
         * todo 如何检查isInterrupt，如何捕获异常
         */
        view.displayIKEAGoods(Resource(Resource.LOADING, "start request IKEA goods"))
        view.displayCarrefourGoods(Resource(Resource.LOADING, "start request carrefour goods"))
        val strWaiting = "wait\n=====================\n====================="
        view.displayBetterGoods(Resource(Resource.LOADING, strWaiting))

        val ikeaFuture = backendExecutor.submit<Goods> {
            backendWork.getGoodsFromIKEA().postToMainThread {
                view.displayIKEAGoods(Resource(Resource.FINISH, it))
            }
        }.addToSet()

        val carrefourFuture = backendExecutor.submit<Goods> {
            backendWork.getGoodsFromCarrefour().postToMainThread {
                view.displayCarrefourGoods(Resource(Resource.FINISH, it))
            }
        }.addToSet()

        backendExecutor.submit<Goods> {
            val ikeaGoods = ikeaFuture.get()
            val carrefourGoods = carrefourFuture.get()

            mainHandler.post {
                view.displayBetterGoods(
                    Resource(
                        Resource.LOADING,
                        "start compare which one is better"
                    )
                )
            }

            backendWork.selectBetterOne(ikeaGoods, carrefourGoods).postToMainThread {
                view.displayBetterGoods(Resource(Resource.FINISH, it))
            }
        }.addToSet()
    }

    override fun cancel() {
        with(futures) {
            forEach {
                it?.get()?.cancel(true)
            }
            clear()
        }
    }
}