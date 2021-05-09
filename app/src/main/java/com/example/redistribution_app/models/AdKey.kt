package com.example.redistribution_app.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class AdKey (
        @PrimaryKey
        var _id: Int = 0,
        var _partition: String = "_partition",
        var name: String = ""

) : RealmObject() {
    class FoodType constructor(val id: Long) {
        private val foodType = FoodType(id)
        fun createInstance() = foodType
    }

}