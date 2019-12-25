package com.jetbrains.edu.javascript.learning.codeforces

import com.jetbrains.edu.javascript.learning.JsConfigurator
import com.jetbrains.edu.learning.EduNames
import com.jetbrains.edu.learning.codeforces.CodeforcesLanguageProvider
import com.jetbrains.edu.learning.configuration.EduConfigurator

class JsCodeforcesLanguageProvider : CodeforcesLanguageProvider {
  override val codeforcesLanguageNamings: List<String> = listOf("JavaScript", "Node.js")
  override val configurator: EduConfigurator<*> = JsConfigurator()
  override val languageId: String = EduNames.JAVASCRIPT
  override val templateFileName: String = "codeforces.main.js"
}