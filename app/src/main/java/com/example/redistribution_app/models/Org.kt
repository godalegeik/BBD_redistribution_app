package com.example.redistribution_app.models

import com.example.redistribution_app.models.Ad
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId

open class Org(
        @PrimaryKey var _id: Int = 0,
        var _partition: String = "_partition",
        var code: String = "",
        var name: String = "",
        var person: String = "",
        var type: String = "",
        var adress: String = "",
        var city: String = "",
        var email: String? = "",
        var phone: String = "",
        var status: String = "",
        var createdAds: RealmList<Ad> = RealmList(),
        var selectedAds: RealmList<Ad> = RealmList(),

        var password: String = ""


    ) : RealmObject() {
}