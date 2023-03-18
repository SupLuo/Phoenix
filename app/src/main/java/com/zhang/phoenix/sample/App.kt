package com.zhang.phoenix.sample

import androidx.multidex.MultiDexApplication
import com.zhang.phoenix.Phoenix

class App : MultiDexApplication() {

    override fun onCreate() {
        Phoenix.init(this,BuildConfig.DEBUG)
        super.onCreate()
    }
}