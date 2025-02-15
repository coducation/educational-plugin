package com.jetbrains.edu.coursecreator.stepik

import com.jetbrains.edu.learning.EduTestCase
import com.jetbrains.edu.learning.course
import com.jetbrains.edu.learning.courseFormat.CourseMode
import com.jetbrains.edu.learning.stepik.StepikTestUtils.logOutFakeStepikUser
import com.jetbrains.edu.learning.stepik.StepikTestUtils.loginFakeStepikUser
import com.jetbrains.edu.learning.stepik.isUpToDate
import junit.framework.TestCase
import java.util.*

class StepikUpdateDateTest : EduTestCase() {

  override fun setUp() {
    super.setUp()
    loginFakeStepikUser()
  }

  override fun tearDown() {
    logOutFakeStepikUser()
    super.tearDown()
  }

  fun `test course up to date`() {
    val course = course(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section2") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
    }.asRemote()

    val courseFromServer = course(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section2") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()

    TestCase.assertEquals(true, course.isUpToDate(courseFromServer))
  }

  fun `test course date changed`() {
    val course = course(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section2") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
    }.asRemote()

    val courseFromServer = course(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section2") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()
    courseFromServer.updateDate = Date()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test course additional materials date changed`() {
    val course = course(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section2") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
    }.asRemote()

    val courseFromServer = course(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section2") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()

    courseFromServer.updateDate = Date()
    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test course section added`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section2") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section2") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section3") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test course section removed`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section2") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test course lesson added`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
      section("section2") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }

      lesson("lesson1") {
        eduTask {
          taskFile("fizz.kt")
        }
      }

      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test course lesson removed`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
          taskFile("fizz.kt")
        }
      }
      lesson("lesson2") {
        eduTask {
          taskFile("fizz.kt")
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
          taskFile("fizz.kt")
        }
      }
    }.asRemote()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test all top-level lessons removed`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
          taskFile("fizz.kt")
        }
      }
      lesson("lesson2") {
        eduTask {
          taskFile("fizz.kt")
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }

      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test section date changed`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }

      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()

    courseFromServer.sections.single().updateDate = Date()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test lesson date changed`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
          taskFile("fizz.kt")
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
          taskFile("fizz.kt")
        }
      }
    }.asRemote()

    courseFromServer.lessons.single().updateDate = Date()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test lesson from section date changed`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }

      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()

    courseFromServer.sections.single().lessons.single().updateDate = Date()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test lesson added into section`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
        lesson("lesson2") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }

      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()

    courseFromServer.sections.single().updateDate = Date()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test lesson removed from section`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
        lesson("lesson2") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section("section1") {
        lesson("lesson1") {
          eduTask {
            taskFile("fizz.kt")
          }
        }
      }

      lesson("PyCharm additional materials") {
        eduTask { }
      }
    }.asRemote()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test task added`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
          taskFile("fizz.kt")
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
        }
        eduTask {
        }
      }
    }.asRemote()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test task removed`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
        }
        eduTask {
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
        }
      }
    }.asRemote()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))
  }

  fun `test task date changed`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
        }
      }
    }.asRemote()

    val courseFromServer = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson("lesson1") {
        eduTask {
        }
      }
    }.asRemote()

    courseFromServer.lessons.single().taskList.single().updateDate = Date()

    TestCase.assertEquals(false, course.isUpToDate(courseFromServer))

  }
}