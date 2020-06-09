package com.jetbrains.edu.learning.stepik

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffDialogHints
import com.intellij.diff.DiffManager
import com.intellij.diff.chains.SimpleDiffRequestChain
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.ui.ColorUtil
import com.intellij.ui.JBColor
import com.jetbrains.edu.learning.EduNames
import com.jetbrains.edu.learning.EduUtils
import com.jetbrains.edu.learning.courseFormat.Course
import com.jetbrains.edu.learning.courseFormat.ext.getVirtualFile
import com.jetbrains.edu.learning.courseFormat.tasks.Task
import com.jetbrains.edu.learning.courseFormat.tasks.TheoryTask
import com.jetbrains.edu.learning.courseFormat.tasks.choice.ChoiceTask
import com.jetbrains.edu.learning.messages.EduCoreBundle
import com.jetbrains.edu.learning.stepik.api.Reply
import com.jetbrains.edu.learning.stepik.api.SolutionFile
import com.jetbrains.edu.learning.stepik.api.Submission
import com.jetbrains.edu.learning.taskDescription.ui.AdditionalTabPanel
import com.jetbrains.edu.learning.taskDescription.ui.EduBrowserHyperlinkListener
import com.jetbrains.edu.learning.taskDescription.ui.styleManagers.StyleManager
import icons.EducationalCoreIcons
import java.net.URL
import java.text.DateFormat
import java.util.*
import java.util.stream.Collectors
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import kotlin.math.roundToInt

object SubmissionsUiProvider {

  fun createSubmissionsTab(task: Task?, course: Course, project: Project): AdditionalTabPanel? {
    if (task == null || task is TheoryTask) return null
    val submissionsProvider = SubmissionsProvider.getSubmissionsProviderForCourse(course) ?: return null
    val submissionsManager = SubmissionsManager.getInstance(project)
    if (!submissionsProvider.submissionsCanBeShown(course)) return null

    val descriptionText = StringBuilder()
    val submissionsPanel = AdditionalTabPanel(project, SubmissionsManager.SUBMISSIONS_TAB_NAME)
    val submissionsList = submissionsManager.getSubmissionsFromMemory(task.id)

    if (submissionsProvider.isLoggedIn() || submissionsList != null) {
      if (submissionsList == null) return null
      when {
        task is ChoiceTask -> addViewOnStepikLink(descriptionText, task, submissionsPanel)
        submissionsList.isEmpty() -> descriptionText.append(
          "<a ${StyleManager().textStyleHeader}>${EduCoreBundle.message("submissions.empty")}")
        else -> {
          addSubmissionsToText(submissionsList, descriptionText)
          submissionsPanel.addHyperlinkListener(getSubmissionsListener(task, project, submissionsManager))
        }
      }
    }
    else {
      addLoginLink(descriptionText, submissionsPanel, submissionsProvider)
    }

    submissionsPanel.setText(descriptionText.toString())
    return submissionsPanel
  }

  private fun addViewOnStepikLink(descriptionText: StringBuilder, currentTask: ChoiceTask, submissionsPanel: AdditionalTabPanel) {
    descriptionText.append(
      "<a ${StyleManager().textStyleHeader};color:${ColorUtil.toHex(hyperlinkColor())} " +
      "href=https://stepik.org/submissions/${currentTask.id}?unit=${currentTask.lesson.unitId}\">" +
      EduCoreBundle.message("submissions.view.quiz.on.stepik", "</a><a ${StyleManager().textStyleHeader}>"))
    submissionsPanel.addHyperlinkListener(EduBrowserHyperlinkListener.INSTANCE)
  }

  private fun addLoginLink(descriptionText: StringBuilder,
                           submissionsPanel: AdditionalTabPanel,
                           submissionsProvider: SubmissionsProvider) {
    descriptionText.append("<a ${StyleManager().textStyleHeader};color:${ColorUtil.toHex(hyperlinkColor())}" +
                           " href=>${EduCoreBundle.message("submissions.login", submissionsProvider.getPlatformName())}" +
                           "</a><a ${StyleManager().textStyleHeader}>")
    submissionsPanel.addHyperlinkListener(HyperlinkListener { e ->
      if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
        submissionsProvider.doAuthorize()
      }
    })
  }

  private fun getSubmissionsListener(task: Task, project: Project, submissionsManager: SubmissionsManager): HyperlinkListener {
    return HyperlinkListener { e ->
      if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
        val submission = submissionsManager.getSubmissionsFromMemory(task.id)?.find { it.id.toString() == e.description }
                         ?: return@HyperlinkListener
        val reply = submission.reply ?: return@HyperlinkListener
        runInEdt {
          showDiff(project, task, reply)
        }
      }
    }
  }

  private fun hyperlinkColor() = JBColor(0x6894C6, 0x5C84C9)

  private fun getImageUrl(status: String?): URL? {
    val icon = when (status) {
      EduNames.CORRECT -> EducationalCoreIcons.TaskSolvedNoFrame
      else -> EducationalCoreIcons.TaskFailedNoFrame
    }
    return (icon as IconLoader.CachedImageIcon).url
  }

  private fun getLinkColor(submission: Submission): String = when (submission.status) {
    EduNames.CORRECT -> "#${ColorUtil.toHex(JBColor(0x368746, 0x499C54))}"
    else -> "#${ColorUtil.toHex(JBColor(0xC7222D, 0xFF5261))}"
  }

  private fun getSubmissionTexts(reply: Reply, taskName: String): Map<String, String>? {
    val solutions = reply.solution
    if (solutions == null) {
      val submissionText = reply.code ?: return null
      return mapOf(taskName to submissionText)
    }
    return solutions.stream().collect(Collectors.toMap(SolutionFile::name, SolutionFile::text))
  }

  private fun formatDate(time: Date): String {
    val calendar = GregorianCalendar()
    calendar.time = time
    val formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault())
    return formatter.format(calendar.time)
  }

  private fun addSubmissionsToText(submissionsNext: List<Submission>,
                                   descriptionText: StringBuilder) {
    for (submission in submissionsNext) {
      descriptionText.append(submissionLink(submission)).append("<br>")
    }
  }

  private fun showDiff(project: Project, task: Task, reply: Reply) {
    val taskFiles = task.taskFiles.values.toMutableList()
    val submissionTexts = getSubmissionTexts(reply, task.name) ?: return
    val requests = taskFiles.mapNotNull {
      val virtualFile = it.getVirtualFile(project) ?: error("VirtualFile for ${it.name} not found")
      val currentFileContent = DiffContentFactory.getInstance().create(VfsUtil.loadText(virtualFile), virtualFile.fileType)
      val submissionText = submissionTexts[it.name] ?: submissionTexts[task.name]
      if (EduUtils.isTestsFile(project, virtualFile) || submissionText == null) {
        null
      }
      else {
        val submissionFileContent = DiffContentFactory.getInstance().create(StepikSolutionsLoader.removeAllTags(submissionText),
                                                                            virtualFile.fileType)
        SimpleDiffRequest(EduCoreBundle.message("submissions.compare"),
                          currentFileContent,
                          submissionFileContent,
                          EduCoreBundle.message("submissions.local"),
                          EduCoreBundle.message("submissions.submission"))
      }
    }
    DiffManager.getInstance().showDiff(project, SimpleDiffRequestChain(requests), DiffDialogHints.FRAME)
  }

  private fun submissionLink(submission: Submission): String? {
    val time = submission.time ?: return null
    val pictureSize = (StyleManager().bodyFontSize * 0.75).roundToInt()
    val text = formatDate(time)
    return "<h><img src=${getImageUrl(submission.status)} hspace=6 width=${pictureSize} height=${pictureSize}/></h>" +
           "<a ${StyleManager().textStyleHeader};color:${getLinkColor(submission)} href=${submission.id}> ${text}</a>"
  }
}
