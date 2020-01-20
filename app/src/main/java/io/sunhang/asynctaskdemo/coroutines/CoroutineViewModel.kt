package io.sunhang.asynctaskdemo.coroutines

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.sunhang.asynctaskdemo.model.Course

class CoroutineViewModel : ViewModel() {
    val allCourses = MutableLiveData<List<Course>>()

    fun requestServer() {

    }
}