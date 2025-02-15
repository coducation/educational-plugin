package com.jetbrains.edu.learning.stepik

import com.intellij.openapi.project.Project
import com.jetbrains.edu.learning.EduSettings
import com.jetbrains.edu.learning.courseFormat.Course
import com.jetbrains.edu.learning.courseFormat.EduCourse
import com.jetbrains.edu.learning.courseFormat.ext.allTasks
import com.jetbrains.edu.learning.courseFormat.tasks.CodeTask
import com.jetbrains.edu.learning.courseFormat.tasks.EduTask
import com.jetbrains.edu.learning.courseFormat.tasks.Task
import com.jetbrains.edu.learning.courseFormat.tasks.choice.ChoiceTask
import com.jetbrains.edu.learning.statistics.EduCounterUsageCollector.AuthorizationPlace
import com.jetbrains.edu.learning.stepik.api.StepikBasedSubmission
import com.jetbrains.edu.learning.stepik.api.StepikConnector
import com.jetbrains.edu.learning.submissions.SubmissionsProvider

class StepikSubmissionsProvider : SubmissionsProvider {

  override fun loadAllSubmissions(project: Project, course: Course): Map<Int, List<StepikBasedSubmission>> {
    val submissionsById = mutableMapOf<Int, List<StepikBasedSubmission>>()
    if (course is EduCourse && course.isStepikRemote && isLoggedIn()) {
      val allTasks: List<Task> = course.allTasks
      for (task in allTasks) {
        if (task is ChoiceTask) {
          submissionsById[task.id] = listOf()
        }
        else if (task is CodeTask || task is EduTask) {
          submissionsById.putAll(loadSubmissions(listOf(task), course.id))
        }
      }
    }
    return submissionsById
  }

  override fun loadSubmissions(tasks: List<Task>, courseId: Int): Map<Int,List<StepikBasedSubmission>> {
    return tasks.associate { Pair(it.id, StepikConnector.getInstance().getSubmissions(it.id)) }
  }

  override fun areSubmissionsAvailable(course: Course): Boolean {
    return course is EduCourse && course.isStudy && course.isStepikRemote
  }

  override fun getPlatformName(): String = StepikNames.STEPIK

  override fun isLoggedIn(): Boolean = EduSettings.isLoggedIn()

  override fun doAuthorize() {
    StepikConnector.getInstance().doAuthorize(authorizationPlace = AuthorizationPlace.SUBMISSIONS_TAB)
  }
}