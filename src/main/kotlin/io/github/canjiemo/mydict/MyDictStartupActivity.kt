package io.github.canjiemo.mydict

import com.intellij.compiler.CompilerConfiguration
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.startup.ProjectActivity

class MyDictStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        if (!isMyDictOnClasspath(project)) return
        if (isAnnotationProcessingEnabled(project)) return

        NotificationGroupManager.getInstance()
            .getNotificationGroup("MyDict")
            .createNotification(
                "MyDict requires annotation processing",
                "Enable annotation processing to allow MyDict to generate xxxDesc fields at compile time.",
                NotificationType.WARNING
            )
            .addAction(OpenAnnotationProcessingSettingsAction())
            .notify(project)
    }

    private fun isMyDictOnClasspath(project: Project): Boolean {
        return OrderEnumerator.orderEntries(project)
            .librariesOnly()
            .classes()
            .roots
            .any { root -> root.name.contains("mydict", ignoreCase = true) }
    }

    private fun isAnnotationProcessingEnabled(project: Project): Boolean {
        return try {
            val config = CompilerConfiguration.getInstance(project)
            config.defaultProcessorProfile.isEnabled
        } catch (e: Exception) {
            true // 获取不到配置时，不打扰用户
        }
    }
}
