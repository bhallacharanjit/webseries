package com.aprosoft.webseries.Shared

import android.content.Context
import android.content.SharedPreferences
import com.kaopiz.kprogresshud.KProgressHUD
import org.json.JSONObject

class Singleton {

    val imageUrl ="http://webseries.aprosoftech.com/Images/"
    val prefName = "WebseriesPref"
    val userPref = "UserPref"


    fun setSharedPrefrence(context: Context, jsonObject: JSONObject){
        val sharedPreferences:SharedPreferences=context.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(userPref,jsonObject.toString()).apply()
    }
    fun getUserFromSharedPrefrence(context: Context):JSONObject?{
        val sharedPreferences:SharedPreferences =context.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        return if (sharedPreferences.contains(userPref)){

            val userString=sharedPreferences.getString(userPref,"")
            if (userString.equals("",true)){
                null
            }else{
                JSONObject(userString)
            }
        }else{
            null
        }
    }

    fun createLoading(context: Context?, title: String?, message: String?): KProgressHUD? {
        return KProgressHUD.create(context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel(title)
            .setDetailsLabel(message)
            .setCancellable(false)
            .setAnimationSpeed(3)
            .setDimAmount(0.4f)
            .show()
    }

}