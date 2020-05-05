package xyz.k4czp3r.facenotify

import android.app.Application

class FaceNotify : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: FaceNotify
            private set
    }

}