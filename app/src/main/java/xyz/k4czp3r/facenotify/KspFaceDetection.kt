package xyz.k4czp3r.facenotify

import xyz.k4czp3r.facenotify.helpers.PrefsKeys
import xyz.k4czp3r.facenotify.helpers.SecureSetting
import xyz.k4czp3r.facenotify.helpers.SecureSettingsHelper
import xyz.k4czp3r.facenotify.helpers.SharedPrefs
import xyz.k4czp3r.facenotify.models.InstructionTypes
import xyz.k4czp3r.facenotify.models.NotificationType
import xyz.k4czp3r.facenotify.models.NotificationTypes

class KspFaceDetection {
    private val secureSettingsHelper = SecureSettingsHelper()
    private val sharedPrefs = SharedPrefs()

    fun restoreDefaultNotificationSettings(){
        secureSettingsHelper.putStr(SecureSetting.lock_screen_show_notifications, "1")
        secureSettingsHelper.putStr(SecureSetting.lock_screen_allow_private_notifications, "1")
    }

    fun prepareSecureSettingsFor(notificationType: NotificationType){
        when(notificationType.instruction){
            InstructionTypes.whole_notification ->{
                secureSettingsHelper.putStr(SecureSetting.lock_screen_allow_private_notifications, "1")
                secureSettingsHelper.putStr(SecureSetting.lock_screen_show_notifications, "0")
            }
            InstructionTypes.only_content ->{
                secureSettingsHelper.putStr(SecureSetting.lock_screen_show_notifications, "1")
                secureSettingsHelper.putStr(SecureSetting.lock_screen_allow_private_notifications, "0")
            }
        }
    }

    fun showNotifications(){
        when(NotificationTypes[sharedPrefs.getInt(PrefsKeys.SELECTED_NOTIFICATION_MODE)].instruction){
            InstructionTypes.whole_notification -> {
                secureSettingsHelper.putStr(SecureSetting.lock_screen_show_notifications, "1")
            }
            InstructionTypes.only_content -> {
                secureSettingsHelper.putStr(SecureSetting.lock_screen_allow_private_notifications, "1")
            }
        }
    }
    fun hideNotifications(){
        when(NotificationTypes[sharedPrefs.getInt(PrefsKeys.SELECTED_NOTIFICATION_MODE)].instruction){
            InstructionTypes.whole_notification -> {
                secureSettingsHelper.putStr(SecureSetting.lock_screen_show_notifications, "0")
            }
            InstructionTypes.only_content -> {
                secureSettingsHelper.putStr(SecureSetting.lock_screen_allow_private_notifications, "0")
            }
        }
    }

}
