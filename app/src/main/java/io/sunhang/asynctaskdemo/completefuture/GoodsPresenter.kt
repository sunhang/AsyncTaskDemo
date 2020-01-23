package io.sunhang.asynctaskdemo.completefuture

import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import android.os.Looper
import io.sunhang.asynctaskdemo.BaseGoodsPresenter
import io.sunhang.asynctaskdemo.model.BackendWork
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.model.Resource
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Supplier

@TargetApi(Build.VERSION_CODES.N)
class GoodsPresenter : BaseGoodsPresenter() {
    private val futures = mutableSetOf<CompletableFuture<*>>()
    private val backendWork = BackendWork()
    private val backendExecutor = Executors.newCachedThreadPool()
    private val mainThreadExecutor = Executor { command ->
        Handler(Looper.getMainLooper()).post(command)
    }

    override fun requestServer() {
        view.displayIKEAGoods(Resource(Resource.LOADING, "start request IKEA goods"))
        view.displayCarrefourGoods(Resource(Resource.LOADING, "start request carrefour goods"))
        val strWaiting = "wait\n=====================\n====================="
        view.displayBetterGoods(Resource(Resource.LOADING, strWaiting))


        val ikeaFuture = CompletableFuture.supplyAsync(Supplier {
            backendWork.getGoodsFromIKEA()
        }, backendExecutor).apply {
            thenAcceptAsync(Consumer {
                view.displayIKEAGoods(Resource(Resource.FINISH, it))
            }, mainThreadExecutor)

            futures += this
        }

        val carrefourFuture = CompletableFuture.supplyAsync(Supplier {
            backendWork.getGoodsFromCarrefour()
        }, backendExecutor).apply {
            thenAcceptAsync(Consumer {
                view.displayCarrefourGoods(Resource(Resource.FINISH, it))
            }, mainThreadExecutor)

            futures += this
        }

        futures += ikeaFuture.thenCombineAsync(
            carrefourFuture,
            BiFunction<Goods, Goods, Pair<Goods, Goods>> { g0, g1 ->
                val str = "start compare which one is better"
                view.displayBetterGoods(Resource(Resource.LOADING, str))
                Pair(g0, g1)
            },
            mainThreadExecutor
        ).thenApplyAsync(java.util.function.Function<Pair<Goods, Goods>, Goods> {
            backendWork.selectBetterOne(it.first, it.second)
        }, backendExecutor).thenAcceptAsync(Consumer<Goods> {
            view.displayBetterGoods(Resource(Resource.FINISH, it))
        }, mainThreadExecutor)
    }

    override fun cancel() {
        futures.forEach {
            it.cancel(true)
        }
    }
}