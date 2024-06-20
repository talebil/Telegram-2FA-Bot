import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val botVersion = "6.0.7"
val koinVersion = "3.6.0-wasm-alpha2"

plugins {
    kotlin("jvm") version "1.7.10"
    id("app.cash.sqldelight") version "2.0.0-rc01"
}

group = "com.p1neapplexpress.ci"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.p1neapplexpress.ci")
        }
    }
}

dependencies {
    implementation("app.cash.sqldelight:sqlite-driver:2.0.0-rc01")
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:$botVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("dev.turingcomplete:kotlin-onetimepassword:2.4.1")
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}