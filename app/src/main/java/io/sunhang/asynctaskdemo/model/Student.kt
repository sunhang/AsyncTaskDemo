package io.sunhang.asynctaskdemo.model

import io.sunhang.asynctaskdemo.threadInfo

class Student(val id: String) {
    // 所选择课程
    private val selectedCourses = mutableListOf<Course>()

    // 选择课程
    fun chooseCourses(courses: List<Course>) {
        courses.find { it.name == "操作系统" }?.let {
            selectedCourses.add(it)
        }
        courses.find { it.name == "数据库" }?.let {
            selectedCourses.add(it)
        }

        println("finished choosing courses(size:${selectedCourses.size}) ${threadInfo()}")
    }
}
