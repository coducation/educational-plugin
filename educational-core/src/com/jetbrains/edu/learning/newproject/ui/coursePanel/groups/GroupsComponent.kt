package com.jetbrains.edu.learning.newproject.ui.coursePanel.groups

import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.components.JBPanelWithEmptyText
import com.jetbrains.edu.learning.courseFormat.Course
import com.jetbrains.edu.learning.taskDescription.ui.TaskDescriptionView

class GroupsComponent(selectionChanged: () -> Unit) : JBPanelWithEmptyText(VerticalFlowLayout(0, 0)) {

  private val courseGroupModel: CourseGroupModel = CourseGroupModel(selectionChanged)

  init {
    background = TaskDescriptionView.getTaskDescriptionBackgroundColor()
    withEmptyText(NO_COURSES)
  }

  fun addGroup(titleString: String, courses: List<Course>) {
    val groupPanel = CoursesGroupPanel(titleString, courses) { card -> courseGroupModel.addCourseCard(card) }
    add(groupPanel)
  }

  fun clear() {
    courseGroupModel.clear()
    removeAll()
  }

  fun setSelectedValue(newCourseToSelect: Course?) {
    courseGroupModel.setSelection(newCourseToSelect)
  }

  fun initialSelection() {
    courseGroupModel.initialSelection()
  }

  val selectedValue: Course?
    get() = courseGroupModel.selectedCard?.course

  companion object {
    private const val NO_COURSES = "No courses found"
  }
}