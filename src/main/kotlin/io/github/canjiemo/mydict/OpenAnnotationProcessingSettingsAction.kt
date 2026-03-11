package io.github.canjiemo.mydict

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil

class OpenAnnotationProcessingSettingsAction :
    NotificationAction("Enable Annotation Processing") {

    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        ShowSettingsUtil.getInstance().showSettingsDialog(
            e.project,
            "Annotation Processors"
        )
        notification.expire()
    }
}
