@file:Suppress("unused")

package com.jetbrains.edu.learning.stepik.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface StepikService {
  @GET("stepics/1/")
  fun getCurrentUser(): Call<UsersList>

  @POST("enrollments")
  fun enrollments(@Body enrollment: EnrollmentData): Call<Any>

  @GET("enrollments/{id}/")
  fun enrollments(@Path("id") courseId: Int): Call<Any>

  @GET("courses")
  fun courses(@Query("is_idea_compatible") isIdeaCompatible: Boolean,
              @Query("is_public") isPublic: Boolean,
              @Query("page") page: Int,
              @Query("enrolled") enrolled: Boolean?): Call<CoursesList>

  @GET("courses/{id}")
  fun courses(@Path("id") courseId: Int,
              @Query("is_idea_compatible") isIdeaCompatible: Boolean?): Call<CoursesList>

  @GET("users")
  fun users(@Query("ids[]") vararg ids: Int): Call<UsersList>

  @GET("sections")
  fun sections(@Query("ids[]") vararg ids: Int): Call<SectionsList>

  @POST("sections")
  fun sections(@Body sectionData: SectionData): Call<SectionsList>

  @PUT("sections/{id}")
  fun sections(@Path("id") sectionId: Int, @Body sectionData: SectionData): Call<SectionsList>

  @GET("lessons")
  fun lessons(@Query("ids[]") vararg ids: Int): Call<LessonsList>

  @PUT("lessons/{id}")
  fun lessons(@Path("id") lessonId: Int, @Body lessonData: LessonData): Call<LessonsList>

  @POST("lessons")
  fun lessons(@Body lessonData: LessonData): Call<LessonsList>

  @GET("units")
  fun units(@Query("ids[]") vararg ids: Int): Call<UnitsList>

  @GET("units")
  fun lessonUnit(@Query("lesson") lesson: Int): Call<UnitsList>

  @POST("units")
  fun units(@Body unitData: UnitData): Call<UnitsList>

  @PUT("units/{id}")
  fun unit(@Path("id") unitId: Int, @Body unitData: UnitData): Call<UnitsList>

  @GET("steps")
  fun steps(@Query("ids[]") vararg ids: Int): Call<StepsList>

  @GET("progresses")
  fun progresses(@Query("ids[]") vararg ids: String): Call<ProgressesList>

  @GET("submissions")
  fun submissions(@Query("order") order: String = "desc",
                  @Query("page") page: Int = 1,
                  @Query("status") status: String,
                  @Query("step") step: Int): Call<SubmissionsList>

  @GET("submissions")
  fun submissions(@Query("order") order: String = "desc",
                  @Query("attempt") attempt: Int = 1,
                  @Query("user") user: Int): Call<SubmissionsList>

  @POST("submissions")
  fun submissions(@Body submissionData: SubmissionData): Call<SubmissionsList>

  @GET("attempts")
  fun attempts(@Query("step") stepId: Int, @Query("user") userId: Int): Call<AttemptsList>

  @POST("attempts")
  fun attempts(@Body attemptData: AttemptData): Call<AttemptsList>

  @GET("assignments")
  fun assignments(@Query("ids[]") vararg ids: Int): Call<AssignmentsList>

  @POST("views")
  fun view(@Body viewData: ViewData): Call<ResponseBody>
}