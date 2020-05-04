package xyz.k4czp3r.facenotify.helpers

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import xyz.k4czp3r.facenotify.FaceNotify

class PermissionHelper(){
    fun isPermissionGranted(permName: String): Boolean{
        val appContext = FaceNotify.instance.applicationContext
        val packageName = appContext.packageName
        return appContext.packageManager.checkPermission(permName, packageName) == PackageManager.PERMISSION_GRANTED
    }

    /*fun askForPermission(activity: Activity, permNames: Array<String>, requestCode: Int){
        ActivityCompat.requestPermissions(activity,permNames,requestCode)
    }*/
}