package io.sunhang.asynctaskdemo

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.sunhang.asynctaskdemo.model.Resource

interface GoodsView : MvpView{
    fun displayIKEAGoods(resource: Resource)
    fun displayCarrefourGoods(resource: Resource)
    fun displayBetterGoods(resource: Resource)
}