package com.example.crudapp.model

import com.google.firebase.firestore.Exclude

open class TaskId {
    @Exclude
    var TaskId: String? = null
    fun <T : TaskId?> withId(id: String): T {
        TaskId = id
        return this as T
    }
}