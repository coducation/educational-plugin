@file:Suppress("unused")

package com.jetbrains.edu.coursecreator.yaml.format

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.util.StdConverter
import com.jetbrains.edu.coursecreator.yaml.InvalidYamlFormatException
import com.jetbrains.edu.learning.courseFormat.tasks.choice.ChoiceOption
import com.jetbrains.edu.learning.courseFormat.tasks.choice.ChoiceOptionStatus

private const val IS_CORRECT = "is_correct"
private const val OPTIONS = "options"
private const val IS_MULTIPLE_CHOICE = "is_multiple_choice"
private const val FEEDBACK_CORRECT = "message_correct"
private const val FEEDBACK_INCORRECT = "message_incorrect"

@JsonPropertyOrder(TaskYamlMixin.TYPE, IS_MULTIPLE_CHOICE, OPTIONS, FEEDBACK_CORRECT, FEEDBACK_INCORRECT, TaskYamlMixin.FILES,
                   TaskYamlMixin.FEEDBACK_LINK)
abstract class ChoiceTaskYamlMixin : TaskYamlMixin() {

  @JsonProperty(IS_MULTIPLE_CHOICE)
  private var isMultipleChoice: Boolean = false

  @JsonProperty(OPTIONS)
  private lateinit var choiceOptions: List<ChoiceOption>

  @JsonProperty(FEEDBACK_CORRECT)
  var messageCorrect: String = ""

  @JsonProperty(FEEDBACK_INCORRECT)
  var messageIncorrect: String = ""
}

abstract class ChoiceOptionYamlMixin {
  @JsonProperty
  private var text: String = ""

  @JsonProperty(IS_CORRECT)
  @JsonSerialize(converter = FromChoiceOptionStatusConverter::class)
  @JsonDeserialize(converter = ToChoiceOptionStatusConverter::class)
  @JsonInclude(JsonInclude.Include.CUSTOM, valueFilter = UnknownOptionFilter::class)
  private var status: ChoiceOptionStatus = ChoiceOptionStatus.UNKNOWN
}

private class FromChoiceOptionStatusConverter : StdConverter<ChoiceOptionStatus, Boolean>() {
  override fun convert(value: ChoiceOptionStatus): Boolean {
    return when (value) {
      ChoiceOptionStatus.CORRECT -> true
      ChoiceOptionStatus.INCORRECT -> false
      else -> throw InvalidYamlFormatException("Unknown option status not allowed")
    }
  }
}

private class ToChoiceOptionStatusConverter : StdConverter<Boolean?, ChoiceOptionStatus>() {
  override fun convert(value: Boolean?): ChoiceOptionStatus {
    if (value == null) {
      return ChoiceOptionStatus.UNKNOWN
    }
    return if (value) ChoiceOptionStatus.CORRECT else ChoiceOptionStatus.INCORRECT
  }
}

@Suppress("EqualsOrHashCode")
private class UnknownOptionFilter {
  override fun equals(other: Any?): Boolean {
    if (other == null || other !is ChoiceOptionStatus) {
      return false
    }
    return other == ChoiceOptionStatus.UNKNOWN
  }
}