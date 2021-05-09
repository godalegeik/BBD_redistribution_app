package com.example.redistribution_app.utils

import android.content.Context

class UserPreference (context: Context) {




    val preference_name = "SharedPreferenceExample"
    val preference = context.getSharedPreferences(preference_name, Context.MODE_PRIVATE)
    val loggedOrgId = "-1"

    fun getLoggedOrgId(): Int{
        return preference.getInt(loggedOrgId, 0)
    }


    fun setLoggedOrgId(id: Int){
        val editor = preference.edit()
        editor.putInt(loggedOrgId, id)
        editor.apply()
    }

    //    val preference_login_count = "LoginCoutn"

//    fun setLoginCOunt(count: Int){
//        val editor = preference.edit()
//        editor.putInt(preference_login_count, count)
//        editor.apply()
//    }

    //    fun getLoginCount(): Int{
//        return preference.getInt(preference_login_count, 0)
//    }
}