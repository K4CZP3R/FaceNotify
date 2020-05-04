package xyz.k4czp3r.facenotify.helpers

import android.util.Log
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import kotlin.math.log

class TimeHelpers {
    private fun convertLogcatTime(logLine: String): String{
        val chars : CharArray = CharArray(logLine.length)
        for(i in logLine.indices){
            var ch : Char = logLine[i]
            if(ch >= 0x0660.toChar() && ch <= 0x0669.toChar()){
                ch -= 0x06f0.toChar() - '0';
            }
            else if(ch >= 0x06f0.toChar() && ch <= 0x06f9.toChar()){
                ch -= 0x06f0.toChar() - '0';
            }
            chars[i] = ch;
        }
        return String(chars)
    }

    private fun timeStringToMs(input: String): Long{
        val pattern = Pattern.compile("\\d+-\\d+\\s+(\\d+):(\\d+):(\\d+)\\.(\\d+)")
        val matcher = pattern.matcher(input)
        if(matcher.find()){
            return matcher.group(1)!!.toLong() * 3600000L+
                    matcher.group(2)!!.toLong() * 60000 +
                    matcher.group(3)!!.toLong() * 1000+
                    matcher.group(4)!!.toLong()
        }
        else{
            throw IllegalArgumentException("Invalid format: '${input}'")
        }
    }

    fun getLogcatTimeMs(logLine: String): Long{
        val logLineConverted = convertLogcatTime(logLine)
        return timeStringToMs(logLineConverted)


    }

    fun getCurrentDayTime(): String{
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss.SSS"))
    }
    fun getCurrentDayTimeMs(): Long{
        return timeStringToMs(getCurrentDayTime())
    }


}