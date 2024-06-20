package com.p1neapplexpress.ci.data

import com.github.kotlintelegrambot.entities.CallbackQuery
import com.p1neapplexpress.ci.util.QueryParamEncoder

data class CallbackQueryData(
    val command: String,
    val params: HashMap<String, String>
) {
    fun toCallbackQuery(): String = "$command?${QueryParamEncoder.encodeParams(params)}"
}

fun CallbackQuery.callbackQueryData() = CallbackQueryData(
    command = data.let { it.substring(0, it.indexOf("?")) },
    params = QueryParamEncoder.decodeParams(data.let { it.substring(it.indexOf("?") + 1) })
)
