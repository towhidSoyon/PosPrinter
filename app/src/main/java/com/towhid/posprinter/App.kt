package com.towhid.posprinter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.posprinter.POSConnect

@HiltAndroidApp
class App : Application(){
    override fun onCreate() {
        super.onCreate()

        // Initialize POSConnect library
        POSConnect.init(this)
    }
}