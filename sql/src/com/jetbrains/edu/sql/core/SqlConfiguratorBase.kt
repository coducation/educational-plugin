package com.jetbrains.edu.sql.core

import com.jetbrains.edu.learning.EduExperimentalFeatures
import com.jetbrains.edu.learning.configuration.EduConfigurator
import com.jetbrains.edu.learning.isFeatureEnabled
import icons.DatabaseIcons
import javax.swing.Icon

interface SqlConfiguratorBase<Settings : Any>: EduConfigurator<Settings> {
  override val logo: Icon
    get() = DatabaseIcons.Sql

  override val isEnabled: Boolean
    get() = isFeatureEnabled(EduExperimentalFeatures.SQL_COURSES)

  companion object {
    const val TASK_SQL: String = "task.sql"
  }
}
