buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.android.tools.build:gradle:7.0.4") // или ваша версия Gradle
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0") // или ваша версия Kotlin
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}