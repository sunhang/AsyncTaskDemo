package io.sunhang.asynctaskdemo.rx

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.plugins.RxJavaPlugins
import io.sunhang.asynctaskdemo.BaseGoodsPresenter
import io.sunhang.asynctaskdemo.model.Goods
import io.sunhang.asynctaskdemo.model.Resource
import io.sunhang.asynctaskdemo.plusAssign

class GoodsPresenter : BaseGoodsPresenter() {
    private val goodsModel = GoodsModel()
    private val compositeDisposable = CompositeDisposable()

    /**
     * What's different in 2.0 > Error handling
     * <https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling>
     */
    init {
        RxJavaPlugins.setErrorHandler { e: Throwable? ->
            if (e is InterruptedException) { // fine, some blocking code was interrupted by a dispose call
                e.printStackTrace()
                return@setErrorHandler
            }
        }
    }

    override fun requestServer() {
        view.displayIKEAGoods(Resource(Resource.LOADING, "start request IKEA goods"))
        view.displayCarrefourGoods(Resource(Resource.LOADING, "start request carrefour goods"))
        val strWaiting = "wait\n=====================\n====================="
        view.displayBetterGoods(Resource(Resource.LOADING, strWaiting))

        val ikeaObservable = goodsModel.getGoodsFromIKEAAsync()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                view.displayIKEAGoods(Resource(Resource.FINISH, it))
            }

        val carrefourObservable = goodsModel.getGoodsFromCarrefourAsync()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                view.displayCarrefourGoods(Resource(Resource.FINISH, it))
            }

        compositeDisposable += Observable.zip(
            ikeaObservable,
            carrefourObservable,
            BiFunction { t1: Goods, t2: Goods ->
                val str = "start compare which one is better"
                view.displayBetterGoods(Resource(Resource.LOADING, str))
                Pair(t1, t2)
            }).flatMap {
            goodsModel.selectBetterOneAsync(it.first, it.second)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.displayBetterGoods(Resource(Resource.FINISH, it))
            }
    }

    override fun cancel() {
        compositeDisposable.dispose()
    }

}