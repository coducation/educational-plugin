package com.jetbrains.edu.learning.checkio.checker

import com.intellij.openapi.application.ex.ApplicationUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.jetbrains.edu.learning.EduUtils
import com.jetbrains.edu.learning.courseFormat.CheckResult
import com.jetbrains.edu.learning.courseFormat.CheckResult.Companion.failedToCheck
import com.jetbrains.edu.learning.checker.EnvironmentChecker
import com.jetbrains.edu.learning.checker.TaskChecker
import com.jetbrains.edu.learning.checker.details.CheckDetailsView.Companion.getInstance
import com.jetbrains.edu.learning.checkio.connectors.CheckiOOAuthConnector
import com.jetbrains.edu.learning.courseFormat.CheckStatus
import com.jetbrains.edu.learning.courseFormat.tasks.EduTask
import com.jetbrains.edu.learning.messages.EduCoreBundle
import org.jetbrains.annotations.NonNls

class CheckiOTaskChecker(
  task: EduTask,
  private val envChecker: EnvironmentChecker,
  project: Project,
  oAuthConnector: CheckiOOAuthConnector,
  @NonNls interpreterName: String,
  @NonNls testFormTargetUrl: String
) : TaskChecker<EduTask>(task, project) {

  private val missionCheck = if (EduUtils.hasJCEF()) {
    CheckiOMissionCheck(project, task, oAuthConnector, interpreterName, testFormTargetUrl)
  } else error("CheckiOTaskChecker needs JCEF")

  override fun check(indicator: ProgressIndicator): CheckResult {
    return try {
      val possibleError = envChecker.getEnvironmentError(project, task)
      if (possibleError != null) {
        return possibleError
      }

      val checkResult = ApplicationUtil.runWithCheckCanceled(missionCheck, ProgressManager.getInstance().progressIndicator)

      if (checkResult.status != CheckStatus.Unchecked) {
        getInstance(project).showResult(EduCoreBundle.message("tab.title.checkio.response"), missionCheck.panel)
      }
      checkResult
    }
    catch (e: Exception) {
      LOG.warn(e.message)
      failedToCheck
    }
  }
}