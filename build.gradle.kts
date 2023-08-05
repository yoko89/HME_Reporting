buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.8.10")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")

    }


}
plugins {
    id("com.android.application") version("8.1.0") apply false
    id("com.android.library") version("8.1.0") apply false
    id("com.google.dagger.hilt.android") version("2.44") apply false
    id("org.jetbrains.kotlin.android") version("1.8.10") apply false
}

tasks.register("clean",Delete::class) {
    delete(rootProject.buildDir)
}
