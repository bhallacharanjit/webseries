package com.aprosoft.webseries.Retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface APIInterface {

    @GET("viewTrailer")
    fun viewTrailer():Call<ResponseBody>

    @GET("viewCategory")
    fun viewCategory():Call<ResponseBody>

    @GET("viewAllSeries")
    fun viewAllsWebseries():Call<ResponseBody>

    @FormUrlEncoded
    @POST("viewCast")
    fun viewCast(@FieldMap params:HashMap<String, String>):Call<ResponseBody>

    @FormUrlEncoded
    @POST("signin")
    fun login(@FieldMap params: HashMap<String, String>):Call<ResponseBody>

    @FormUrlEncoded
    @POST("signup")
    fun signup(@FieldMap params: HashMap<String, String>):Call<ResponseBody>

    @FormUrlEncoded
    @POST("addReview")
    fun addReview(@FieldMap params: HashMap<String, String>):Call<ResponseBody>

    @FormUrlEncoded
    @POST("viewReview")
    fun review(@FieldMap params: HashMap<String, String>):Call<ResponseBody>

    @FormUrlEncoded
    @POST("profile")
    fun profile(@FieldMap params: HashMap<String, String>):Call<ResponseBody>

    @FormUrlEncoded
    @POST("seriesByCategory")
    fun seriesByCategory(@FieldMap params: HashMap<String, String>):Call<ResponseBody>

    @FormUrlEncoded
    @POST("addtomylist")
    fun addtoList(@FieldMap params: HashMap<String, String>):Call<ResponseBody>

    @FormUrlEncoded
    @POST("removefromlist")
    fun removeFromList(@FieldMap params: HashMap<String, String>):Call<ResponseBody>

    @FormUrlEncoded
    @POST("showmylist")
    fun myShowsList(@FieldMap params: HashMap<String, String>):Call<ResponseBody>

    @GET("viewPlatform")
    fun allPlatforms():Call<ResponseBody>

    @FormUrlEncoded
    @POST("platformSeries")
    fun platformSeries(@FieldMap params: HashMap<String, String>):Call<ResponseBody>

    @FormUrlEncoded
    @POST("notificationOnOff")
    fun notification(@FieldMap params: HashMap<String, String>):Call<ResponseBody>


}