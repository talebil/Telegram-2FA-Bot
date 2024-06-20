package com.p1neapplexpress.ci.data.locale

import com.google.gson.annotations.SerializedName

data class LocaleConfig(
    val locales: List<Locale>,
    @SerializedName("default_locale")
    val defaultLocale: String
)
