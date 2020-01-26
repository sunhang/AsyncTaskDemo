package io.sunhang.asynctaskdemo.model

data class Resource(val status: Int, val any: Any? = null) {
    companion object {
        const val LOADING = 0
        const val WAITING = 1
        const val FINISH = 2
    }
}
