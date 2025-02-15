package com.jetbrains.edu.python.learning;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.edu.learning.StudyTaskManager;
import com.jetbrains.python.psi.impl.PyImportResolver;
import com.jetbrains.python.psi.resolve.PyQualifiedNameResolveContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PyEduImportResolver implements PyImportResolver {
  @Nullable
  public PsiElement resolveImportReference(@NotNull QualifiedName name, PyQualifiedNameResolveContext context, boolean withRoots) {
    if (StudyTaskManager.getInstance(context.getProject()).getCourse() == null) {
      return null;
    }
    final String nameString = name.toString();
    PsiFile containingFile = context.getFootholdFile();
    if (containingFile == null) return null;

    final PsiDirectory directory = containingFile.getContainingDirectory();
    if (directory == null) return null;
    final PsiFile file = directory.findFile(nameString + ".py");
    if (file != null) {
      return file;
    }

    return null;
  }
}
