package com.jetbrains.edu.learning;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.jetbrains.edu.learning.actions.StudyAfterCheckAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StudyPluginConfigurator {
  ExtensionPointName<StudyPluginConfigurator> EP_NAME = ExtensionPointName.create("Edu.studyPluginConfigurator");

  /**
   * Provide action group that should be placed on the tool window toolbar.
   */
  @NotNull
  DefaultActionGroup getActionGroup(Project project);

  @Nullable
  StudyAfterCheckAction[] getAfterCheckActions();

  boolean accept(@NotNull final Project project);
}
