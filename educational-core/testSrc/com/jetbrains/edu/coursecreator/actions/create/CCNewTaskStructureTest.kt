package com.jetbrains.edu.coursecreator.actions.create

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.PlainTextLanguage

class CCNewTaskStructureTest : CCNewTaskStructureTestBase() {

  override val language: Language get() = PlainTextLanguage.INSTANCE
  override val settings: Any get() = Unit

  fun `test create edu task`() = checkEduTaskCreation(
    fullTaskStructure = {
      file("task.md")
      file("Task.txt")
      dir("tests") {
        file("Tests.txt")
      }
    },
    taskStructureWithoutSources = {
      file("task.md")
      dir("tests") {
        file("Tests.txt")
      }
    }
  )

  fun `test create output task`() = checkOutputTaskCreation(
    fullTaskStructure = {
      file("task.md")
      file("Main.txt")
      dir("tests") {
        file("input.txt")
        file("output.txt")
      }
    },
    taskStructureWithoutSources = {
      file("task.md")
      dir("tests") {
        file("input.txt")
        file("output.txt")
      }
    }
  )

  fun `test create theory task`() = checkTheoryTaskCreation(
    fullTaskStructure = {
      file("task.md")
      file("Main.txt")
    },
    taskStructureWithoutSources = {
      file("task.md")
    }
  )

  fun `test create IDE task`() = checkIdeTaskCreation(
    fullTaskStructure = {
      file("task.md")
      file("Main.txt")
    },
    taskStructureWithoutSources = {
      file("task.md")
    }
  )

  fun `test create choice task`() = checkChoiceTaskCreation(
    fullTaskStructure = {
      file("task.md")
      file("Main.txt")
    },
    taskStructureWithoutSources = {
      file("task.md")
    }
  )
}
