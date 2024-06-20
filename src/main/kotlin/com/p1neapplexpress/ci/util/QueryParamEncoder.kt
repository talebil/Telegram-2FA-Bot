package com.p1neapplexpress.ci.util

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object QueryParamEncoder {
    @Throws(UnsupportedEncodingException::class)
    fun encodeParams(params: Map<String, String>): String {
        val result = StringBuilder()
        for ((key, value) in params) {
            if (result.isNotEmpty()) {
                result.append("&")
            }
            result.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
            result.append("=")
            result.append(URLEncoder.encode(value, StandardCharsets.UTF_8))
        }
        return result.toString()
    }

    @Throws(UnsupportedEncodingException::class)
    fun decodeParams(encodedParams: String): HashMap<String, String> {
        val params: HashMap<String, String> = HashMap()
        val pairs = encodedParams.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            val key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8)
            val value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8)
            params[key] = value
        }
        return params
    }
}