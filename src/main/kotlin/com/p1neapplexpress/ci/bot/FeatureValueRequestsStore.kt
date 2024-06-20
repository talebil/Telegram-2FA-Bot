package com.p1neapplexpress.ci.bot

import com.p1neapplexpress.ci.feature.AbstractFeature
import java.util.*

data class ValueRequest(
    val requestingFeature: AbstractFeature,
    val key: String? = null
)

object FeatureValueRequestsStore {
    val requests = Stack<ValueRequest>()
}