
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
        id("com.google.devtools.ksp") version "2.0.0-1.0.22"
        id("org.jetbrains.kotlin.jvm") version "2.0.21"
        id("org.jetbrains.kotlin.android") version "2.0.21"
        // Plugin Compose Compiler
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
//        id("com.google.devtools.ksp") version "2.0.0-1.0.22" apply false
//        id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ITForum"
include(":app")
