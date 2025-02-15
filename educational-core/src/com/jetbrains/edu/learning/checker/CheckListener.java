package com.jetbrains.edu.learning.checker;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.jetbrains.edu.learning.courseFormat.CheckResult;
import com.jetbrains.edu.learning.courseFormat.tasks.Task;
import org.jetbrains.annotations.NotNull;

public interface CheckListener {
  ExtensionPointName<CheckListener> EP_NAME = ExtensionPointName.create("Educational.checkListener");

  default void beforeCheck(@NotNull Project project, @NotNull Task task) {}

  default void afterCheck(@NotNull Project project, @NotNull Task task, @NotNull CheckResult result) {}
}
