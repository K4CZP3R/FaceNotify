package xyz.k4czp3r.facenotify

import android.util.Log
import xyz.k4czp3r.facenotify.helpers.TimeHelpers
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

class KspLogcat{
    private var TAG = "KspLogcat"

    val clearLogsCommand = "logcat -c"
    fun clearLogs(){
          try{
              Runtime.getRuntime().exec(clearLogsCommand)
          }
          catch (exception: Exception){
              Log.e(TAG, exception.message)
          }
    }
    fun readLogs(regexFilter: String): ArrayList<String>{
        val listLogcatMirror = ArrayList<String>()

        try{
            val process = Runtime.getRuntime().exec(arrayOf("/system/bin/logcat","-e$regexFilter","-d"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            val allLines = reader.readLines()
            Log.i(TAG, "Got ${allLines.size} lines of logcat!")
            reader.close()
            for(line in allLines){
                if(line.startsWith("-")) continue
                listLogcatMirror.add(line)
            }
        }catch (e: Exception){
            Log.e(TAG, e.message)
            e.printStackTrace()
        }
        return listLogcatMirror
    }
    fun readLogs(includeString: String, startCheckDateTime: String): ArrayList<String>{
        val listLogcatMirror = ArrayList<String>()
        try{
            val process = Runtime.getRuntime().exec(arrayOf("/system/bin/logcat","-t$startCheckDateTime"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            val allLines = reader.readLines()
            Log.i(TAG, "Got ${allLines.size} lines of logcat!")
            reader.close()
            for(line in allLines){
                if(line.startsWith("-")) continue
                if(line.contains(includeString)) listLogcatMirror.add(line)
            }
        }
        catch (e: Exception){
            Log.e(TAG,e.message)
            e.printStackTrace()
        }

        return listLogcatMirror
    }
}