package io.sunhang.asynctaskdemo.coroutines

import io.sunhang.asynctaskdemo.model.Goods

interface IView {
    fun displayGoodsA(goods: Goods)
    fun displayGoodsB(goods: Goods)
    fun displayBetterGoods(goods: Goods)
}