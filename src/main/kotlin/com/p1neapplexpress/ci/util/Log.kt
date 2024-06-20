package com.p1neapplexpress.ci.util

import com.p1neapplexpress.ci.DEBUG
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

object Log {
    private val logFileName: String
        get() = "log${System.currentTimeMillis() / (1000 * 3600 * 24)}.txt"
    private val logFile: File = File(logFileName)

    init {
        logFile.createNewFile()
    }

    private fun getTimeString(): String? {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        return sdf.format(Date())
    }

    @Synchronized
    private fun out(message: String) {
        val logMessage = "[${getTimeString()}] $message"
        println(logMessage)
        logFile.appendText(logMessage + '\n')
    }

    fun exitError(message: String) {
        out(message)
        exitProcess(1)
    }

    fun d(message: String) {
        if (DEBUG) out("[DEBUG] $message")
    }
}