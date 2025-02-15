package com.jetbrains.edu.learning

import com.intellij.openapi.application.AppUIExecutor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.impl.coroutineDispatchingContext
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.UnknownFileType
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.util.ProgressWrapper
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.util.ConcurrencyUtil
import com.intellij.util.PathUtil
import com.jetbrains.edu.learning.courseFormat.Course
import com.jetbrains.edu.learning.courseFormat.TaskFile
import com.jetbrains.edu.learning.courseFormat.isBinary
import com.jetbrains.edu.learning.courseFormat.mimeFileType
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService

private val LOG = Logger.getInstance("openApiExt")

val isUnitTestMode: Boolean get() = ApplicationManager.getApplication().isUnitTestMode

fun checkIsBackgroundThread() {
  check(!ApplicationManager.getApplication().isDispatchThread) {
    "Long running operation invoked on UI thread"
  }
}

fun checkIsWriteActionAllowed() {
  check(ApplicationManager.getApplication().isWriteAccessAllowed) {
    "Write action is required"
  }
}

inline fun invokeLater(modalityState: ModalityState, condition: Condition<*>, crossinline runnable: () -> Unit) {
  ApplicationManager.getApplication().invokeLater({ runnable() }, modalityState, condition)
}

/**
 * Note: there are some unsupported cases in this method.
 * For example, some files have known file type but no extension
 */
fun toEncodeFileContent(path: String): Boolean {
  val name = PathUtil.getFileName(path)
  val extension = FileUtilRt.getExtension(name)
  if (isUnitTestMode && extension == EduUtils.EDU_TEST_BIN) {
    return true
  }
  val fileType = FileTypeManagerEx.getInstanceEx().getFileTypeByExtension(extension)
  if (fileType !is UnknownFileType) {
    return fileType.isBinary
  }
  val contentType = mimeFileType(path) ?: return isGitObject(name)
  return isBinary(contentType)
}

private fun isGitObject(name: String): Boolean {
  return (name.length == 38 || name.length == 40) && name.matches(Regex("[a-z0-9]+"))
}

val Project.courseDir: VirtualFile
  get() {
    val projectDir = guessProjectDir() ?: error("Failed to find course dir for $this")
    if (projectDir.name == Project.DIRECTORY_STORE_FOLDER) {
      return projectDir.parent
    }
    return projectDir
  }

val Project.selectedEditor: Editor? get() = selectedVirtualFile?.getEditor(this)

val Project.selectedVirtualFile: VirtualFile? get() = FileEditorManager.getInstance(this)?.selectedFiles?.firstOrNull()

val Project.selectedTaskFile: TaskFile? get() = selectedVirtualFile?.getTaskFile(this)

val Project.eduState: EduState?
  get() {
    val virtualFile = selectedVirtualFile ?: return null
    val taskFile = virtualFile.getTaskFile(this) ?: return null
    val editor = virtualFile.getEditor(this) ?: return null
    return EduState(virtualFile, editor, taskFile)
  }

val Project.course: Course? get() = StudyTaskManager.getInstance(this).course

val String.xmlEscaped: String get() = StringUtil.escapeXmlEntities(this)

val String.xmlUnescaped: String get() = StringUtil.unescapeXmlEntities(this)

inline fun <T> runReadActionInSmartMode(project: Project, crossinline runnable: () -> T): T {
  return DumbService.getInstance(project).runReadActionInSmartMode(Computable { runnable() })
}

fun Document.toPsiFile(project: Project): PsiFile? {
  return PsiDocumentManager.getInstance(project).getPsiFile(this)
}

fun <T> computeUnderProgress(project: Project? = null,
                             title: String,
                             canBeCancelled: Boolean = true,
                             computation: (ProgressIndicator) -> T): T =
  ProgressManager.getInstance().run(object : Task.WithResult<T, Exception>(project, title, canBeCancelled) {
    override fun compute(indicator: ProgressIndicator): T {
      return computation(indicator)
    }
  })

fun runInBackground(project: Project? = null, title: String, canBeCancelled: Boolean = true, task: (ProgressIndicator) -> Unit) =
  ProgressManager.getInstance().run(object : Task.Backgroundable(project, title, canBeCancelled) {
    override fun run(indicator: ProgressIndicator) = task(indicator)
  })

fun <T : Any> invokeAllWithProgress(tasks: List<() -> T?>, executor: ExecutorService): List<T> {
  val progressManager = ProgressManager.getInstance()
  val indicator = progressManager.progressIndicator
  val callables = tasks.map { task ->
    Callable {
      if (indicator != null) {
        progressManager.runProcess(task, ProgressWrapper.wrap(indicator))
      }
      else {
        task()
      }
    }
  }

  val result = ConcurrencyUtil.invokeAll(callables, executor)
    .filterNot { it.isCancelled }
    .mapNotNull { it.get() }

  ProgressManager.checkCanceled()
  return result
}

fun <T> withRegistryKeyOff(key: String, action: () -> T): T {
  val registryValue = Registry.get(key)
  val before = try {
    registryValue.asBoolean()
  }
  catch (e: MissingResourceException) {
    LOG.error(e)
    return action()
  }

  try {
    registryValue.setValue(false)
    return action()
  }
  finally {
    registryValue.setValue(before)
  }
}

fun <V> getInEdt(modalityState: ModalityState = ModalityState.defaultModalityState(), compute: () -> V): V {
  return runBlocking(AppUIExecutor.onUiThread(modalityState).coroutineDispatchingContext()) {
    compute()
  }
}