package com.jetbrains.edu.learning.newproject.ui.coursePanel

import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.FilterComponent
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBPanelWithEmptyText
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.jetbrains.edu.learning.LanguageSettings
import com.jetbrains.edu.learning.courseFormat.Course
import com.jetbrains.edu.learning.messages.EduCoreBundle
import com.jetbrains.edu.learning.newproject.ui.CoursesPanel
import com.jetbrains.edu.learning.newproject.ui.ErrorState
import com.jetbrains.edu.learning.newproject.ui.courseSettings.CourseSettings
import java.awt.BorderLayout
import java.awt.CardLayout
import java.util.*
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
import javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
import javax.swing.event.DocumentListener

const val DESCRIPTION_AND_SETTINGS_TOP_OFFSET = 25

private const val ERROR_LABEL_TOP_GAP = 20
private const val HORIZONTAL_MARGIN = 20

private const val EMPTY = "empty"
private const val CONTENT = "content"

class CoursePanel(
  private val isLocationFieldNeeded: Boolean,
  private val joinCourseAction: (CourseInfo, CourseMode, JPanel) -> Unit
) : JPanel() {
  var errorState: ErrorState = ErrorState.NothingSelected
  var course: Course? = null

  private val header: HeaderPanel = HeaderPanel(HORIZONTAL_MARGIN) { courseInfo, courseMode -> joinCourse(courseInfo, courseMode) }
  private val description = CourseDescriptionPanel(HORIZONTAL_MARGIN)
  private val advancedSettings = CourseSettings(isLocationFieldNeeded, HORIZONTAL_MARGIN)
  private val errorLabel: HyperlinkLabel = HyperlinkLabel().apply { isVisible = false }
  private var mySearchField: FilterComponent? = null
  private val listeners: MutableList<CoursesPanel.CourseValidationListener> = ArrayList()

  private fun joinCourse(courseInfo: CourseInfo, courseMode: CourseMode) {
    joinCourseAction(courseInfo, courseMode, this)
  }

  val locationString: String?
    get() = advancedSettings.locationString

  val projectSettings: Any?
    get() = advancedSettings.getProjectSettings()

  val languageSettings: LanguageSettings<*>?
    get() = advancedSettings.languageSettings

  init {
    layout = CardLayout()
    border = JBUI.Borders.customLine(DIVIDER_COLOR, 0, 0, 0, 0)

    val emptyStatePanel = JBPanelWithEmptyText().withEmptyText(EduCoreBundle.message("course.dialog.no.course.selected"))
    add(emptyStatePanel, EMPTY)

    val content = JPanel(VerticalFlowLayout(0, 0))
    content.add(header)
    content.add(description)
    content.add(advancedSettings)
    content.add(createErrorPanel())
    val scrollPane = JBScrollPane(content, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER).apply {
      border = null
    }
    add(scrollPane, CONTENT)

    UIUtil.setBackgroundRecursively(this, MAIN_BG_COLOR)
  }

  fun setButtonsEnabled(isEnabled: Boolean) {
    header.setButtonsEnabled(isEnabled)
  }

  fun addLocationFieldDocumentListener(listener: DocumentListener) {
    advancedSettings.addLocationFieldDocumentListener(listener)
  }

  fun showEmptyState() {
    (layout as CardLayout).show(this, EMPTY)
  }

  private fun updateCourseDescriptionPanel(course: Course, settings: CourseDisplaySettings = CourseDisplaySettings()) {
    val location = locationString
    if (location == null && isLocationFieldNeeded) {
      // TODO: set error
      return
    }
    header.update(CourseInfo(course, { locationString }, { advancedSettings.languageSettings }), settings)
    description.bind(course)
  }

  private fun createErrorPanel(): JPanel {
    val errorPanel = JPanel(BorderLayout())
    errorPanel.add(errorLabel, BorderLayout.CENTER)
    errorPanel.border = JBUI.Borders.empty(ERROR_LABEL_TOP_GAP, HORIZONTAL_MARGIN, 0, 0)
    addErrorStateListener()
    UIUtil.setBackgroundRecursively(errorPanel, MAIN_BG_COLOR)
    return errorPanel
  }

  private fun addErrorStateListener() {
    errorLabel.addHyperlinkListener(ErrorStateHyperlinkListener())
  }

  fun bindCourse(course: Course, settings: CourseDisplaySettings = CourseDisplaySettings()): LanguageSettings<*>? {
    (layout as CardLayout).show(this, CONTENT)
    this.course = course
    advancedSettings.update(course, settings.showLanguageSettings)
    updateCourseDescriptionPanel(course, settings)
    revalidate()
    repaint()
    return advancedSettings.languageSettings
  }

  fun notifyListeners(canStartCourse: Boolean) {
    for (listener in listeners) {
      listener.validationStatusChanged(canStartCourse)
    }
  }

  fun addCourseValidationListener(listener: CoursesPanel.CourseValidationListener) {
    listeners.add(listener)
    listener.validationStatusChanged(canStartCourse())
  }

  fun validateSettings(course: Course?) = advancedSettings.validateSettings(course)

  fun bindSearchField(searchField: FilterComponent) {
    mySearchField = searchField
  }

  fun hideErrorPanel() {
    errorLabel.isVisible = false
  }

  fun setError(errorState: ErrorState) {
    this.errorState = errorState
    val message = errorState.message
    header.setButtonToolTip(null)
    if (message != null) {
      when (errorState) {
        is ErrorState.JetBrainsAcademyLoginNeeded -> {
          errorLabel.isVisible = true
          errorLabel.setHyperlinkText(message.beforeLink, message.linkText, message.afterLink)
          header.setButtonToolTip(EduCoreBundle.message("course.dialog.login.required"))
        }
        else -> {
          errorLabel.isVisible = true
          errorLabel.setHyperlinkText(message.beforeLink, message.linkText, message.afterLink)
          header.setButtonToolTip(message.beforeLink + message.linkText + message.afterLink)
        }
      }
    }
    else {
      errorLabel.isVisible = false
    }
    errorLabel.foreground = errorState.foregroundColor
  }

  private fun canStartCourse(): Boolean = errorState.courseCanBeStarted

  companion object {
    // default divider's color too dark in Darcula, so use the same color as in plugins dialog
    val DIVIDER_COLOR = JBColor(0xC5C5C5, 0x515151)
  }

}

