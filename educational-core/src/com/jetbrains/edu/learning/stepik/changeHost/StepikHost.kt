package com.jetbrains.edu.learning.stepik.changeHost

import com.intellij.ide.util.PropertiesComponent
import com.jetbrains.edu.learning.stepik.StepikNames.STEPIK_DEFAULT_URL
import com.jetbrains.edu.learning.stepik.StepikNames.STEPIK_DEV_URL
import com.jetbrains.edu.learning.stepik.StepikNames.STEPIK_HOST_ORDINAL_PROPERTY
import com.jetbrains.edu.learning.stepik.StepikNames.STEPIK_RELEASE_URL
import com.jetbrains.edu.learning.stepik.StepikOAuthBundle.valueOrDefault

enum class StepikHost(val url: String, val clientId: String, val clientSecret: String) {

  PRODUCTION(STEPIK_DEFAULT_URL, valueOrDefault("stepikClientId", ""), valueOrDefault("stepikClientSecret", "")),
  RELEASE(STEPIK_RELEASE_URL, valueOrDefault("stepikTestClientId", ""), valueOrDefault("stepikTestClientSecret", "")),
  DEV(STEPIK_DEV_URL, valueOrDefault("stepikTestClientId", ""), valueOrDefault("stepikTestClientSecret", ""));

  override fun toString(): String {
    return url
  }

  companion object {
    fun getSelectedHost(): StepikHost = values()[PropertiesComponent.getInstance().getInt(STEPIK_HOST_ORDINAL_PROPERTY, PRODUCTION.ordinal)]
  }
}