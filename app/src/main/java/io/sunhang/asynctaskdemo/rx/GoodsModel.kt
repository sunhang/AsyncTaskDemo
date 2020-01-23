package io.sunhang.asynctaskdemo.rx

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.sunhang.asynctaskdemo.model.BackendWork
import io.sunhang.asynctaskdemo.model.Goods


class GoodsModel {
    private val backendWork = BackendWork()

    /**
     * 从宜家买桌子
     */
    fun getGoodsFromIKEAAsync(): Observable<Goods> {
        return Observable.fromCallable {
            backendWork.getGoodsFromIKEA()
        }.subscribeOn(Schedulers.io())
    }

    /**
     * 从家乐福买桌子
     */
    fun getGoodsFromCarrefourAsync(): Observable<Goods>{
        return Observable.fromCallable {
            backendWork.getGoodsFromCarrefour()
        }.subscribeOn(Schedulers.io())
    }

    /**
     * 比较出更好的桌子
     */
    fun selectBetterOneAsync(
        ikeaGoods: Goods,
        carrefourGoods: Goods
    ): Observable<Goods> {
        return Observable.fromCallable {
            backendWork.selectBetterOne(ikeaGoods, carrefourGoods)
        }.subscribeOn(Schedulers.computation())
    }
}