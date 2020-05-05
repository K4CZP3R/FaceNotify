package xyz.k4czp3r.facenotify.models

import xyz.k4czp3r.facenotify.FaceNotify
import xyz.k4czp3r.facenotify.R

enum class InstructionTypes {
    whole_notification, only_content
}

data class NotificationType(val id: Int, val name: String, val instruction: InstructionTypes)

val HIDE_WHOLE_NOTIFICATION = NotificationType(
    0,
    FaceNotify.instance.applicationContext.getString(R.string.notification_mode_whole),
    InstructionTypes.whole_notification
)
val HIDE_ONLY_CONTENT = NotificationType(
    1,
    FaceNotify.instance.applicationContext.getString(R.string.notification_mode_content),
    InstructionTypes.only_content
)

val NotificationTypes = arrayOf(HIDE_WHOLE_NOTIFICATION, HIDE_ONLY_CONTENT)
