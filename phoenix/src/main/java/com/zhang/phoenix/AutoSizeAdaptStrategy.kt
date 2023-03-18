package com.zhang.phoenix

import android.app.Activity
import me.jessyan.autosize.DefaultAutoAdaptStrategy

class AutoSizeAdaptStrategy : DefaultAutoAdaptStrategy() {
    override fun applyAdapt(target: Any?, activity: Activity?) {
        if(target is PnxAdapt){
            super.applyAdapt(target, activity)
        }
    }
}