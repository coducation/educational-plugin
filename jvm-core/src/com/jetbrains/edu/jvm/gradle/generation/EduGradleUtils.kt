package com.jetbrains.edu.jvm.gradle.generation

import com.intellij.openapi.externalSystem.service.execution.ExternalSystemJdkUtil
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemJdkUtil.USE_INTERNAL_JAVA
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemJdkUtil.USE_PROJECT_JDK
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.JavaSdkVersion
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ContainerUtilRt
import com.jetbrains.edu.learning.courseGeneration.GeneratorUtils.createFileFromTemplate
import org.jetbrains.plugins.gradle.settings.DistributionType
import org.jetbrains.plugins.gradle.settings.GradleProjectSettings
import org.jetbrains.plugins.gradle.util.GradleConstants
import java.io.File
import java.io.IOException

object EduGradleUtils {
  @JvmStatic
  fun isConfiguredWithGradle(project: Project): Boolean {
    return File(project.basePath, GradleConstants.DEFAULT_SCRIPT_NAME).exists()
  }

  @JvmStatic
  @Throws(IOException::class)
  fun createProjectGradleFiles(
    projectDir: VirtualFile,
    templates: Map<String, String>,
    templateVariables: Map<String, Any>
  ) {
    for ((name, templateName) in templates) {
      createFileFromTemplate(projectDir, name, templateName, templateVariables)
    }
  }

  @JvmOverloads
  @JvmStatic
  fun setGradleSettings(project: Project, sdk: Sdk?, location: String, distributionType: DistributionType = DistributionType.WRAPPED) {
    val systemSettings = ExternalSystemApiUtil.getSettings(project, GradleConstants.SYSTEM_ID)
    val existingProject = ExternalSystemApiUtil.getSettings(project, GradleConstants.SYSTEM_ID).getLinkedProjectSettings(location)
    if (existingProject is GradleProjectSettings) {
      if (existingProject.distributionType == null) {
        existingProject.distributionType = distributionType
      }
      if (existingProject.externalProjectPath == null) {
        existingProject.externalProjectPath = location
      }
      setUpGradleJvm(existingProject, sdk)
      return
    }

    val gradleProjectSettings = GradleProjectSettings()
    gradleProjectSettings.distributionType = distributionType
    // BACKCOMPAT: 2019.3
    @Suppress("DEPRECATION")
    gradleProjectSettings.isUseAutoImport = true
    gradleProjectSettings.externalProjectPath = location
    // IDEA runner is much more faster and it doesn't write redundant messages into console.
    // Note, it doesn't affect tests - they still are run with gradle runner
    gradleProjectSettings.delegatedBuild = false
    setUpGradleJvm(gradleProjectSettings, sdk)

    val projects = ContainerUtilRt.newHashSet<Any>(systemSettings.getLinkedProjectsSettings())
    projects.add(gradleProjectSettings)
    systemSettings.setLinkedProjectsSettings(projects)
    ExternalSystemUtil.ensureToolWindowInitialized(project, GradleConstants.SYSTEM_ID)
  }

  private fun setUpGradleJvm(projectSettings: GradleProjectSettings, sdk: Sdk?) {
    if (sdk == null) return
    val projectSdkVersion = sdk.javaSdkVersion
    val internalJdk = ExternalSystemJdkUtil.resolveJdkName(null, USE_INTERNAL_JAVA)
    val internalSdkVersion = internalJdk?.javaSdkVersion

    // Try to avoid incompatibility between gradle and jdk versions
    projectSettings.gradleJvm = when {
      internalSdkVersion == null -> USE_PROJECT_JDK
      projectSdkVersion == null -> USE_INTERNAL_JAVA
      else -> if (internalSdkVersion < projectSdkVersion) USE_INTERNAL_JAVA else USE_PROJECT_JDK
    }
  }

  private val Sdk.javaSdkVersion: JavaSdkVersion? get() = JavaSdk.getInstance().getVersion(this)
}
