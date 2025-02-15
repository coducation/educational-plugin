package com.jetbrains.edu.javascript.learning;

import com.intellij.javascript.nodejs.interpreter.NodeInterpreterUtil;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.util.UserDataHolder;
import com.jetbrains.edu.javascript.learning.messages.EduJavaScriptBundle;
import com.jetbrains.edu.learning.EduNames;
import com.jetbrains.edu.learning.LanguageSettings;
import com.jetbrains.edu.learning.courseFormat.Course;
import com.jetbrains.edu.learning.messages.EduCoreBundle;
import com.jetbrains.edu.learning.newproject.ui.errors.SettingsValidationResult;
import com.jetbrains.edu.learning.newproject.ui.errors.ValidationMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class JsLanguageSettings extends LanguageSettings<JsNewProjectSettings> {
  private final JsNewProjectSettings mySettings = new JsNewProjectSettings();
  private final NodeJsInterpreterField myInterpreterField;

  public JsLanguageSettings() {
    Project defaultProject = ProjectManager.getInstance().getDefaultProject();
    myInterpreterField = new NodeJsInterpreterField(defaultProject, false) {
      @Override
      public boolean isDefaultProjectInterpreterField() {
        return true;
      }
    };
    myInterpreterField.addChangeListener(interpreter -> mySettings.setSelectedInterpreter(interpreter));
    myInterpreterField.setInterpreterRef(NodeJsInterpreterManager.getInstance(defaultProject).getInterpreterRef());
  }

  @NotNull
  @Override
  public JsNewProjectSettings getSettings() {
    return mySettings;
  }

  @NotNull
  @Override
  public List<LabeledComponent<JComponent>> getLanguageSettingsComponents(@NotNull Course course,
                                                                          @NotNull Disposable disposable,
                                                                          @Nullable UserDataHolder context) {
    return Collections.singletonList(
      LabeledComponent.create(myInterpreterField, EduCoreBundle.message("select.interpreter"), BorderLayout.WEST));
  }

  @NotNull
  @Override
  public SettingsValidationResult validate(@Nullable Course course, @Nullable String courseLocation) {
    NodeJsInterpreter interpreter = myInterpreterField.getInterpreter();
    String message = NodeInterpreterUtil.validateAndGetErrorMessage(interpreter);
    if (message == null) {
      return SettingsValidationResult.OK;
    }
    ValidationMessage validationMessage = new ValidationMessage(
      EduJavaScriptBundle.message("configure.js.environment.help", message, EduNames.ENVIRONMENT_CONFIGURATION_LINK_JS),
      EduNames.ENVIRONMENT_CONFIGURATION_LINK_JS
    );
    return new SettingsValidationResult.Ready(validationMessage);
  }
}
