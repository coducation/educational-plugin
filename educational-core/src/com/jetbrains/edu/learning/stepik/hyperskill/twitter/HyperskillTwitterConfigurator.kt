package com.jetbrains.edu.learning.stepik.hyperskill.twitter

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.BuildNumber
import com.jetbrains.edu.learning.course
import com.jetbrains.edu.learning.courseFormat.CheckStatus
import com.jetbrains.edu.learning.courseFormat.tasks.Task
import com.jetbrains.edu.learning.isUnitTestMode
import com.jetbrains.edu.learning.messages.EduCoreBundle
import com.jetbrains.edu.learning.stepik.hyperskill.courseFormat.HyperskillCourse
import com.jetbrains.edu.learning.twitter.TwitterPluginConfigurator
import com.jetbrains.edu.learning.twitter.TwitterUtils
import java.nio.file.Path
import kotlin.random.Random

class HyperskillTwitterConfigurator : TwitterPluginConfigurator {

  override fun askToTweet(project: Project, solvedTask: Task, statusBeforeCheck: CheckStatus): Boolean {
    if (ApplicationInfo.getInstance().build < BUILD_202 && !isUnitTestMode) return false
    val course = project.course as? HyperskillCourse ?: return false
    if (!course.isStudy) return false
    if (statusBeforeCheck == CheckStatus.Solved) return false

    val projectLesson = course.getProjectLesson() ?: return false
    if (solvedTask.lesson != projectLesson) return false

    var allProjectTaskSolved = true
    projectLesson.visitTasks {
      allProjectTaskSolved = allProjectTaskSolved && it.status == CheckStatus.Solved
    }
    return allProjectTaskSolved
  }

  override fun getDefaultMessage(solvedTask: Task): String {
    val course = solvedTask.course
    val courseName = (course as? HyperskillCourse)?.getProjectLesson()?.presentableName ?: course.presentableName
    return EduCoreBundle.message("hyperskill.twitter.message", courseName)
  }

  override fun getImagePath(solvedTask: Task): Path? {
    val gifIndex = Random.Default.nextInt(NUMBER_OF_IMAGES)
    return TwitterUtils.pluginRelativePath("twitter/hyperskill/achievement$gifIndex.gif")
  }

  companion object {
    // BACKCOMPAT: 2020.1
    private val BUILD_202 = BuildNumber.fromString("202")!!

    private const val NUMBER_OF_IMAGES = 3
  }
}
