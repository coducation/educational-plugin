package com.jetbrains.edu.cpp

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.io.IOUtil
import com.jetbrains.cidr.cpp.toolchains.CPPToolchains
import com.jetbrains.cmake.completion.CMakeRecognizedCPPLanguageStandard.CPP11
import com.jetbrains.cmake.completion.CMakeRecognizedCPPLanguageStandard.CPP14
import com.jetbrains.cmake.completion.CMakeRecognizedCPPLanguageStandard.values
import com.jetbrains.edu.learning.EduNames
import com.jetbrains.edu.learning.LanguageSettings
import com.jetbrains.edu.learning.courseFormat.Course
import com.jetbrains.edu.learning.newproject.ui.ErrorMessage
import com.jetbrains.edu.learning.stepik.course.StepikCourse
import java.awt.BorderLayout
import javax.swing.JComponent

class CppLanguageSettings : LanguageSettings<CppProjectSettings>() {

  private var languageStandard: String = CPP14.standard
  private val defaultToolchain = CPPToolchains.getInstance().defaultToolchain

  override fun getSettings(): CppProjectSettings = CppProjectSettings(languageStandard)

  override fun getLanguageSettingsComponents(course: Course): List<LabeledComponent<JComponent>> {
    val standards = when (course) {
      is StepikCourse -> arrayOf(CPP11.standard, languageStandard)
      else -> languageVersions.toTypedArray()
    }

    val langStandardComboBox = ComboBox(standards)
    langStandardComboBox.selectedItem = languageStandard

    langStandardComboBox.addItemListener {
      languageStandard = it.item.toString()
      notifyListeners()
    }

    return listOf<LabeledComponent<JComponent>>(LabeledComponent.create(langStandardComboBox, "C++ Standard", BorderLayout.WEST))
  }

  override fun getLanguageVersions(): List<String> {
    return values().map { it.standard }
  }

  override fun validate(course: Course?, courseLocation: String?): ErrorMessage? = when {
    courseLocation != null && SystemInfo.isWindows && !IOUtil.isAscii(courseLocation) -> {
      val environment = defaultToolchain?.toolSet?.name
      val details = if (environment != null) "with ${environment} " else ""
      ErrorMessage("${EduNames.WARNING}: ", "Location should contain only ASCII characters, " +
                                            "CMake ${details}might not work properly")
    }
    else -> null
  }
}
