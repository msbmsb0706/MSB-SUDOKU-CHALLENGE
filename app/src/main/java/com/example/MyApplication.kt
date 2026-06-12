package com.example

import android.app.Application

class MyApplication : Application() {
    override fun getAttributionTag(): String? {
        return "default"
    }
}
