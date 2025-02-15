package com.jetbrains.edu.learning.checker

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.jetbrains.edu.learning.checker.CheckUtils.NOT_RUNNABLE_MESSAGE
import com.jetbrains.edu.learning.checker.CheckUtils.createDefaultRunConfiguration
import com.jetbrains.edu.learning.checker.CheckUtils.getCustomRunConfiguration
import com.jetbrains.edu.learning.courseFormat.CheckResult
import com.jetbrains.edu.learning.courseFormat.CheckStatus
import com.jetbrains.edu.learning.courseFormat.tasks.TheoryTask
import com.jetbrains.edu.learning.isUnitTestMode

open class TheoryTaskChecker(task: TheoryTask, project: Project) : TaskChecker<TheoryTask>(task, project) {

  override fun check(indicator: ProgressIndicator): CheckResult {
    val configuration = getRunConfiguration()
    if (configuration == null) {
      return CheckResult(CheckStatus.Unchecked, NOT_RUNNABLE_MESSAGE)
    }

    val processListener = if (isUnitTestMode) StdoutProcessListener() else null

    if (!CheckUtils.executeRunConfigurations(project, listOf(configuration), indicator, processListener = processListener)) {
      LOG.warn("Execution failed")
      return CheckResult.failedToCheck
    }

    return if (isUnitTestMode) {
      CheckResult(CheckStatus.Solved, processListener?.output.orEmpty().joinToString(""))
    }
    else {
      CheckResult.SOLVED
    }
  }

  protected open fun getRunConfiguration(): RunnerAndConfigurationSettings? {
    return getCustomRunConfiguration(project, task) ?: createDefaultRunConfiguration()
  }

  protected open fun createDefaultRunConfiguration(): RunnerAndConfigurationSettings? {
    return createDefaultRunConfiguration(project, task)
  }

  companion object {
    private val LOG = Logger.getInstance(TheoryTaskChecker::class.java)
  }
}
