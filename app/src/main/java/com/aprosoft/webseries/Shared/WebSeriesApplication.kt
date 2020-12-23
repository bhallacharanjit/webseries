package com.aprosoft.webseries.Shared

import android.app.Application
import com.chartboost.sdk.Chartboost

class WebSeriesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Chartboost.startWithAppId(applicationContext,"5fe19ff7d94f9310315fe6e4","1691cc74876f2616c284538dde7b7efe49b11a56")
    }
}