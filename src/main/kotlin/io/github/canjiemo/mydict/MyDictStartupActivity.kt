package io.github.canjiemo.mydict

import com.intellij.compiler.CompilerConfiguration
import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope

class MyDictStartupActivity : ProjectActivity {

    private companion object {
        const val NOTIFIED_KEY = "mydict.annotation.processing.notified"
        const val MY_DICT_ANNOTATION_FQN = "io.github.canjiemo.tools.dict.MyDict"
    }

    override suspend fun execute(project: Project) {
        if (!isMyDictOnClasspath(project)) return
        if (isAnnotationProcessingEnabled(project)) return

        val props = PropertiesComponent.getInstance(project)
        if (props.getBoolean(NOTIFIED_KEY, false)) return
        props.setValue(NOTIFIED_KEY, true)

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
        val scope = GlobalSearchScope.allScope(project)
        return JavaPsiFacade.getInstance(project)
            .findClass(MY_DICT_ANNOTATION_FQN, scope) != null
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
