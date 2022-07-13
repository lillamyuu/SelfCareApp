package com.example.myapplication

data class Task(
    var id: Int,
    var description: String,
    var cost: String,
    var cat: String,
    var available: Int,
    var dl: Int,
    var check: Int,
    var upd: Boolean = false
)

data class TaskList(var items: List<Task>)