package com.jetbrains.edu;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class EducationalCoreIcons {
  private static Icon load(String path) {
    return IconLoader.getIcon(path, EducationalCoreIcons.class);
  }

  public static final Icon JavaLogo = load("/icons/com/jetbrains/edu/learning/JavaLogo.svg"); // 16x16
  public static final Icon KotlinLogo = load("/icons/com/jetbrains/edu/learning/KotlinLogo.svg"); // 16x16
  public static final Icon ScalaLogo = load("/icons/com/jetbrains/edu/learning/ScalaLogo.svg"); // 16x16
  public static final Icon AndroidLogo = load("/icons/com/jetbrains/edu/learning/AndroidLogo.svg"); // 16x16
  public static final Icon PythonLogo = load("/icons/com/jetbrains/edu/learning/PythonLogo.svg"); // 16x16
  public static final Icon JsLogo = load("/icons/com/jetbrains/edu/learning/JavaScriptLogo.svg"); // 16x16
  public static final Icon RustLogo = load("/icons/com/jetbrains/edu/learning/RustLogo.svg"); // 16x16
  public static final Icon CppLogo = load("/icons/com/jetbrains/edu/learning/CAndC++Logo.svg"); // 16x16
  public static final Icon GoLogo = load("/icons/com/jetbrains/edu/learning/GoLogo.svg"); // 16x16
  public static final Icon PhpLogo = load("/icons/com/jetbrains/edu/learning/PhpLogo.svg"); // 16x16

  public static final Icon WatchInput = load("/icons/com/jetbrains/edu/learning/WatchInput.png"); // 24x24

  public static final Icon Stepik = load("/icons/com/jetbrains/edu/learning/Stepik.png"); // 16x16
  public static final Icon StepikRefresh = load("/icons/com/jetbrains/edu/learning/StepikRefresh.png"); // 16x16
  public static final Icon StepikCourseTab = load("/icons/com/jetbrains/edu/learning/stepikTab.svg"); // 16x16

  public static final Icon CheckiO = load("/icons/com/jetbrains/edu/learning/PyCheckiO.svg");
  public static final Icon JSCheckiO = load("/icons/com/jetbrains/edu/learning/JSCheckiO.svg");

  public static final Icon JB_ACADEMY = load("/icons/com/jetbrains/edu/learning/JB_academy.svg");
  public static final Icon JB_ACADEMY_TAB = load("/icons/com/jetbrains/edu/learning/JB_academy_course_tab.svg"); // 24x24

  public static final Icon Codeforces = load("/icons/com/jetbrains/edu/learning/codeforces.svg"); // 24x24
  public static final Icon CODEFORCES_SMALL = load("/icons/com/jetbrains/edu/learning/codeforcesSmall.svg"); // 16x16
  public static final Icon CodeforcesGrayed = load("/icons/com/jetbrains/edu/learning/codeforcesGrayed.svg");
  public static final Icon LOGGED_IN_USER = load("/icons/com/jetbrains/edu/learning/loggedInUser.svg"); // 16x16
  public static final Icon Coursera = load("/icons/com/jetbrains/edu/learning/coursera.svg"); // 24x24

  public static final Icon MARKETPLACE = load("/icons/com/jetbrains/edu/learning/marketplace_courses.svg"); // 16x16
  public static final Icon MARKETPLACE_TAB = load("/icons/com/jetbrains/edu/learning/marketplace_courses_tab.svg"); // 24x24

  public static final Icon Student = load("/icons/com/jetbrains/edu/Learner.svg"); // 180x180
  public static final Icon StudentHover = load("/icons/com/jetbrains/edu/LearnerActive.svg"); // 180x180
  public static final Icon Teacher = load("/icons/com/jetbrains/edu/Teacher.svg"); // 180x180
  public static final Icon TeacherHover = load("/icons/com/jetbrains/edu/TeacherActive.svg"); // 180x180

  public static final Icon Task = load("/icons/com/jetbrains/edu/eduTaskDefault.png"); // 16x16
  public static final Icon TaskSolved = load("/icons/com/jetbrains/edu/eduTaskDone.png"); // 16x16
  public static final Icon TaskSolvedNoFrame = load("/icons/com/jetbrains/edu/eduTaskDoneNoFrame@2x.png"); //11x11
  public static final Icon TaskSolvedNoFrameHighContrast = load("/icons/com/jetbrains/edu/eduTaskDoneNoFrameHighContrast@2x.png"); //11x11
  public static final Icon TaskFailed = load("/icons/com/jetbrains/edu/eduTaskFailed.png"); // 16x16
  public static final Icon TaskFailedNoFrame = load("/icons/com/jetbrains/edu/eduTaskFailedNoFrame@2x.png"); // 11x11
  public static final Icon TaskFailedNoFrameHighContrast = load("/icons/com/jetbrains/edu/eduTaskFailedNoFrameHighContrast@2x.png");
    // 11x11
  public static final Icon IdeTask = load("/icons/com/jetbrains/edu/eduTaskIdeDefault.png"); // 16x16
  public static final Icon IdeTaskSolved = load("/icons/com/jetbrains/edu/eduTaskIdeDone.png"); // 16x16

  public static final Icon TheoryTask = load("/icons/com/jetbrains/edu/eduTaskTheoryDefault.png"); // 16x16

  public static final Icon TheoryTaskSolved = load("/icons/com/jetbrains/edu/eduTaskTheoryDone.png"); // 16x16

  public static final Icon Lesson = load("/icons/com/jetbrains/edu/eduLessonDefault.png"); // 16x16
  public static final Icon LessonSolved = load("/icons/com/jetbrains/edu/eduLessonDone.png"); // 16x16
  public static final Icon Section = load("/icons/com/jetbrains/edu/eduSectionDefault.png"); // 16x16
  public static final Icon SectionSolved = load("/icons/com/jetbrains/edu/eduSectionDone.png"); // 16x16

  public static final Icon CourseAction = load("/icons/com/jetbrains/edu/eduCourseAction.png"); // 16x16
  public static final Icon CourseTree = load("/icons/com/jetbrains/edu/eduCourseTree.png"); // 16x16
  public static final Icon CourseToolWindow = load("/icons/com/jetbrains/edu/eduCourseTask.svg"); // 13x13

  public static final Icon ResultCorrect = load("/icons/com/jetbrains/edu/learning/resultCorrect.svg"); // 16x16
  public static final Icon ResetTask = load("/icons/com/jetbrains/edu/learning/resetTask.svg"); // 16x16
  public static final Icon CommentTask = load("/icons/com/jetbrains/edu/learning/commentTask.svg"); // 16x16
  public static final Icon Clock = load("/icons/com/jetbrains/edu/learning/clock.svg"); // 16x16

  public static final Icon User = load("/icons/com/jetbrains/edu/usersNumber.svg"); // 12x12

  public static final Icon CheckDetailsIcon = load("/icons/com/jetbrains/edu/learning/checkDetailsToolWindow.svg"); // 13x13

  public static final Icon DOT = load("/icons/com/jetbrains/edu/learning/dot.svg"); // 3x3
}
