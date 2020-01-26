package io.sunhang.asynctaskdemo.threadpool

import android.os.Handler
import android.os.Looper
import io.sunhang.asynctaskdemo.BaseGoodsPresenter
import io.sunhang.asynctaskdemo.model.BackendWork
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.model.Resource
import java.util.concurrent.Executors

class GoodsPresenter : BaseGoodsPresenter() {
    private val backendWork = BackendWork()
    private val backendExecutor = Executors.newCachedThreadPool()
    private val mainHandler = Handler(Looper.getMainLooper())

    private fun Goods.alsoPostToUI(task: (Goods) -> Unit): Goods {
        mainHandler.post {
            task(this)
        }

        return this
    }

    override fun requestServer() {
        view.displayIKEAGoods(Resource(Resource.LOADING))
        view.displayCarrefourGoods(Resource(Resource.LOADING))
        view.displayBetterGoods(Resource(Resource.WAITING))

        val ikeaFuture = backendExecutor.submit<Goods> {
            backendWork.getGoodsFromIKEA().alsoPostToUI {
                view.displayIKEAGoods(Resource(Resource.FINISH, it))
            }
        }

        val carrefourFuture = backendExecutor.submit<Goods> {
            backendWork.getGoodsFromCarrefour().alsoPostToUI {
                view.displayCarrefourGoods(Resource(Resource.FINISH, it))
            }
        }

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

            backendWork.selectBetterOne(ikeaGoods, carrefourGoods).alsoPostToUI {
                view.displayBetterGoods(Resource(Resource.FINISH, it))
            }
        }
    }

    override fun cancel() {
        backendExecutor.shutdownNow()
    }
}