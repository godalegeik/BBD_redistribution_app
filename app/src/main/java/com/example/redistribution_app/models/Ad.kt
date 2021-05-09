package com.example.redistribution_app.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.Ignore
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.bson.types.ObjectId

@RealmClass
open class Ad  (


        @PrimaryKey var _id: Int = 0,
        var _partition: String = "_partition",
        var name: String = "",
        var type: String = "",
        var status: String = "",
        var created: String = "",
        var valid: String = "",
        var comment: String? = null,
        var foodType: String = "",
        var splittable: Boolean = false,

        //    @LinkingObjects("createdAds")
//    val creator: RealmResults<Org>? = null,
//
//    @LinkingObjects("selectedAds")
//    val selector: RealmResults<Org>? = null
        var forOrgs: RealmList<Org>? = RealmList(),

        //  @LinkingObjects("createdAds")
        //val creator: RealmResults<Org>? = null,
        var creator: Org? = null,

        var selector: Org? = null,

        var keys: RealmList<AdKey>? = RealmList()

) : RealmObject()  {
    //toString goes here
    class Ad constructor(val id: Long) {
            private val ad = Ad(id)
            fun createInstance() = ad
        }
    }



