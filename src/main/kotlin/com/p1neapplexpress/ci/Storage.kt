package com.p1neapplexpress.ci

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import comp1neapplexpressci.TFAConfigQueries
import comp1neapplexpressci.UserQueries
import java.io.File

private const val DATABASE_NAME = "database.db"

object Storage {
    lateinit var userQueries: UserQueries
    lateinit var tfaConfigQueries: TFAConfigQueries
    private lateinit var database: Database

    private const val API_KEY_FILENAME: String = "API_KEY"

    fun getToken() = File(API_KEY_FILENAME).readText()

    fun initDB() {
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$DATABASE_NAME")
        Database.Schema.create(driver)
        database = Database(driver)

        userQueries = database.userQueries
        tfaConfigQueries = database.tFAConfigQueries
    }
}