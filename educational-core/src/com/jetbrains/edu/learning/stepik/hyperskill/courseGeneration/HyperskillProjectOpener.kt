package com.jetbrains.edu.learning.stepik.hyperskill.courseGeneration

import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.IdeFrame
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.AppIcon
import com.jetbrains.edu.learning.*
import com.jetbrains.edu.learning.courseFormat.FeedbackLink
import com.jetbrains.edu.learning.courseFormat.Lesson
import com.jetbrains.edu.learning.courseFormat.ext.configurator
import com.jetbrains.edu.learning.courseGeneration.GeneratorUtils
import com.jetbrains.edu.learning.stepik.builtInServer.EduBuiltInServerUtils
import com.jetbrains.edu.learning.stepik.hyperskill.*
import com.jetbrains.edu.learning.stepik.hyperskill.api.HyperskillConnector
import com.jetbrains.edu.learning.stepik.hyperskill.api.HyperskillStepSource
import com.jetbrains.edu.learning.stepik.hyperskill.courseFormat.HyperskillCourse
import com.jetbrains.edu.learning.yaml.YamlFormatSynchronizer

object HyperskillProjectOpener {

  fun openProject(projectId: Int, stageId: Int? = null, stepId: Int? = null): Result<Unit, String> {
    if (focusOpenProject(projectId, stageId)) return Ok(Unit)
    if (openRecentProject(projectId, stageId)) return Ok(Unit)
    return openNewProject(projectId, stageId, stepId)
  }

  private fun openRecentProject(courseId: Int, stageId: Int?): Boolean {
    val (project, course) = EduBuiltInServerUtils.openRecentProject { it is HyperskillCourse && it.hyperskillProject?.id == courseId }
                            ?: return false
    course.putUserData(HYPERSKILL_STAGE, stageId)
    runInEdt { openSelectedStage(course, project, true) }
    return true
  }

  private fun openNewProject(projectId: Int, stageId: Int?, stepId: Int?): Result<Unit, String> {
    return getHyperskillCourseUnderProgress(projectId, stageId, stepId).map { hyperskillCourse ->
      runInEdt {
        requestFocus()
        HyperskillJoinCourseDialog(hyperskillCourse).show()
      }
    }
  }

  private fun focusOpenProject(courseId: Int, stageId: Int?): Boolean {
    val (project, course) = EduBuiltInServerUtils.focusOpenProject { it is HyperskillCourse && it.hyperskillProject?.id == courseId }
                            ?: return false
    course.putUserData(HYPERSKILL_STAGE, stageId)
    runInEdt { openSelectedStage(course, project, true) }
    return true
  }

  fun getHyperskillCourseUnderProgress(projectId: Int, stageId: Int?, stepId: Int?): Result<HyperskillCourse, String> {
    return ProgressManager.getInstance().run(object : Task.WithResult<Result<HyperskillCourse, String>, Exception>
                                                      (null, "Loading project", true) {
      override fun compute(indicator: ProgressIndicator): Result<HyperskillCourse, String> {
        val hyperskillProject = HyperskillConnector.getInstance().getProject(projectId) ?: return Err(FAILED_TO_CREATE_PROJECT)

        if (!hyperskillProject.useIde) {
          return Err(HYPERSKILL_PROJECT_NOT_SUPPORTED)
        }
        val languageId = HYPERSKILL_LANGUAGES[hyperskillProject.language]
        if (languageId == null) {
          return Err("Unsupported language ${hyperskillProject.language}")
        }
        val hyperskillCourse = HyperskillCourse(hyperskillProject, languageId)
        if (hyperskillCourse.configurator == null) {
          return Err("The project isn't supported (language: ${hyperskillProject.language}). " +
                     "Check if all needed plugins are installed and enabled")
        }
        if (stepId != null) {
          val lesson = findOrCreateProblemsLesson(hyperskillCourse, project)
          val lessonDir = lesson.getLessonDir(project)
                          ?: return Err("Could not find Problems directory")

          val stepSource = HyperskillConnector.getInstance().getStepSource(stepId)!!
          findOrCreateTask(hyperskillCourse, lesson, stepSource, lessonDir, project)
        }

        HyperskillConnector.getInstance().fillHyperskillCourse(hyperskillCourse)
        hyperskillCourse.putUserData(HYPERSKILL_STAGE, stageId)

        return Ok(hyperskillCourse)
      }
    })
  }

  // We have to use visible frame here because project is not yet created
  // See `com.intellij.ide.impl.ProjectUtil.focusProjectWindow` implementation for more details
  fun requestFocus() {
    val frame = WindowManager.getInstance().findVisibleFrame()
    if (frame is IdeFrame) {
      AppIcon.getInstance().requestFocus(frame)
    }
    frame.toFront()
  }

  fun findOrCreateProblemsLesson(course: HyperskillCourse, project: Project): Lesson {
    var lesson = course.getLesson(HYPERSKILL_PROBLEMS)
    if (lesson == null) {
      lesson = Lesson()
      lesson.name = HYPERSKILL_PROBLEMS
      lesson.index = course.items.size + 1
      course.addLesson(lesson)
      lesson.init(course, null, false)
      GeneratorUtils.createLesson(lesson, course.getDir(project))
      YamlFormatSynchronizer.saveItem(lesson)
      YamlFormatSynchronizer.saveItem(course)
    }
    return lesson
  }

  fun findOrCreateTask(course: HyperskillCourse, lesson: Lesson, stepSource: HyperskillStepSource,
                       lessonDir: VirtualFile, project: Project): com.jetbrains.edu.learning.courseFormat.tasks.Task {
    var task = lesson.getTask(stepSource.id)
    if (task == null) {
      task = HyperskillConnector.getInstance().getTasks(course, lesson, listOf(stepSource)).first()
      task.name = stepSource.title
      task.feedbackLink = FeedbackLink(stepLink(task.id))
      task.index = lesson.taskList.size + 1
      task.descriptionText = "<b>${task.name}</b> ${openOnHyperskillLink(task.id)}" +
                             "<br/><br/>${task.descriptionText}" +
                             "<br/>${openTheoryLink(stepSource.topicTheory)}"
      lesson.addTask(task)
      task.init(course, lesson, false)

      GeneratorUtils.createTask(task, lessonDir)

      YamlFormatSynchronizer.saveItem(lesson)
      YamlFormatSynchronizer.saveItem(task)
      YamlFormatSynchronizer.saveRemoteInfo(task)

      course.configurator?.courseBuilder?.refreshProject(project, RefreshCause.STRUCTURE_MODIFIED)
    }
    return task
  }

  private fun openTheoryLink(stepId: Int?) =
    if (stepId != null) "<a href=\"${stepLink(stepId)}\">Show topic summary</a>" else ""

  private fun openOnHyperskillLink(stepId: Int) = "<a class=\"right\" href=\"${stepLink(stepId)}\">Open on ${EduNames.JBA}</a>"
}