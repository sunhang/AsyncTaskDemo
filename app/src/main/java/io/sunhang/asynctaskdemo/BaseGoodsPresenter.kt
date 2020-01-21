package io.sunhang.asynctaskdemo

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import io.sunhang.asynctaskdemo.model.Resource

open abstract class BaseGoodsPresenter : MvpBasePresenter<GoodsView>()  {
    private val goodsViewProxy = GoodsViewProxy()

    override fun getView(): GoodsView {
        return goodsViewProxy
    }

    abstract fun requestServer()
    abstract fun cancel()

    inner class GoodsViewProxy : GoodsView {
        override fun displayIKEAGoods(resource: Resource) = ifViewAttached {
            it.displayIKEAGoods(resource)
        }

        override fun displayCarrefourGoods(resource: Resource) = ifViewAttached {
            it.displayCarrefourGoods(resource)
        }

        override fun displayBetterGoods(resource: Resource) = ifViewAttached {
            it.displayBetterGoods(resource)
        }
    }
}