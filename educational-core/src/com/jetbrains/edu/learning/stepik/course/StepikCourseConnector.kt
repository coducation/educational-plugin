package com.jetbrains.edu.learning.stepik.course

import com.intellij.openapi.diagnostic.Logger
import com.jetbrains.edu.learning.Ok
import com.jetbrains.edu.learning.Result
import com.jetbrains.edu.learning.courseFormat.EduCourse
import com.jetbrains.edu.learning.courseFormat.EduLanguage
import com.jetbrains.edu.learning.stepik.PyCharmStepOptions
import com.jetbrains.edu.learning.stepik.StepikLanguage
import com.jetbrains.edu.learning.stepik.StepikTaskBuilder.StepikTaskType
import com.jetbrains.edu.learning.stepik.api.StepikConnector
import com.jetbrains.edu.learning.stepik.api.StepikCourseLoader
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

object StepikCourseConnector : CourseConnector {
  private val LOG = Logger.getInstance(StepikCourseConnector::class.java.name)

  override fun getCourseIdFromLink(link: String): Int {
    try {
      val url = URL(link)
      val pathParts = url.path.split("/").dropLastWhile { it.isEmpty() }
      for (i in pathParts.indices) {
        val part = pathParts[i]
        if (part == "course" && i + 1 < pathParts.size) {
          return Integer.parseInt(pathParts[i + 1])
        }
      }
    }
    catch (e: MalformedURLException) {
      LOG.warn(e.message)
    }

    return -1
  }

  override fun getCourseInfoByLink(link: String): EduCourse? {
    val courseId: Int = try {
      Integer.parseInt(link)
    }
    catch (e: NumberFormatException) {
      getCourseIdFromLink(link)
    }

    if (courseId != -1) {
      return StepikConnector.getInstance().getCourseInfo(courseId)
    }
    return null
  }

  fun getSupportedLanguages(remoteCourse: StepikCourse): Result<List<EduLanguage>, String> {
    val languages = mutableListOf<EduLanguage>()
    try {
      val codeTemplates = getFirstCodeTemplates(remoteCourse)
      for (templateLanguage in codeTemplates.keys) {
        val stepikLanguage = StepikLanguage.langOfName(templateLanguage)
        val stepikLanguageId = stepikLanguage.id ?: continue

        val eduLanguage = EduLanguage(stepikLanguageId, stepikLanguage.version)
        languages.add(eduLanguage)
      }
    }
    catch (e: IOException) {
      LOG.warn(e.message)
    }

    return Ok(languages)
  }

  private fun getFirstCodeTemplates(remoteCourse: StepikCourse): Map<String, String> {
    val unitsIds = StepikCourseLoader.getUnitsIds(remoteCourse)
    val lessons = StepikCourseLoader.getLessonsFromUnitIds(unitsIds)
    for (lesson in lessons) {
      val allStepSources = StepikConnector.getInstance().getStepSources(lesson.stepIds)

      for (stepSource in allStepSources) {
        val step = stepSource.block
        if (step != null && step.name == StepikTaskType.CODE.type && step.options != null) {
          val codeTemplates = (step.options as PyCharmStepOptions).codeTemplates
          if (codeTemplates != null) {
            return codeTemplates
          }
        }
      }
    }
    return emptyMap()
  }
}
