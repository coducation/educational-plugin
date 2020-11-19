package com.jetbrains.edu.html.taskDescription

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTokenType
import com.jetbrains.edu.learning.EduNames
import com.jetbrains.edu.learning.codeInsight.inCourse
import com.jetbrains.edu.learning.codeInsight.inFileWithName
import com.jetbrains.edu.learning.taskDescription.A_TAG
import com.jetbrains.edu.learning.taskDescription.HREF_ATTRIBUTE

object EduHtmlPsiPatterns {

  val inHrefAttributeValue: PsiElementPattern.Capture<PsiElement>
    get() = PlatformPatterns.psiElement(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)
      .inCourse()
      .inFileWithName(EduNames.TASK_HTML)
      .withParent(
        XmlPatterns.xmlAttributeValue(HREF_ATTRIBUTE)
          .withSuperParent(2, XmlPatterns.xmlTag().withName(A_TAG))
      )
}
