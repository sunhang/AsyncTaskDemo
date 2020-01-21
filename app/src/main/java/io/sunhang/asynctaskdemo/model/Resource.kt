package io.sunhang.asynctaskdemo.model

data class Resource(val status: Int, val any: Any) {
    companion object {
        const val LOADING = 0
        const val FINISH = 1
    }
}