buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.53")
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
    }


}
plugins {
    id("com.android.application") version("8.7.3") apply false
    id("com.android.library") version("8.7.3") apply false
    id("com.google.dagger.hilt.android") version("2.53") apply false
    id("org.jetbrains.kotlin.android") version("2.1.0") apply false
    id("com.google.devtools.ksp") version ("2.1.0-1.0.29") apply false
}