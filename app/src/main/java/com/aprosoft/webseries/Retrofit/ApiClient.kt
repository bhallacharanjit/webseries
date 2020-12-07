package com.aprosoft.webseries.Retrofit


import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    var  Base_URL = "http://webseries.aprosoftech.com/api/webseries/"
    val getClient:APIInterface
    get(){
        val gson =GsonBuilder()
            .setLenient()
            .create()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client =OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(15,TimeUnit.MINUTES)
            .writeTimeout(15,TimeUnit.MINUTES)
            .build()

        val retrofit =Retrofit.Builder()
            .baseUrl(Base_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(APIInterface::class.java)
    }
}