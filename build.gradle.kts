buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.52")
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.0.21")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    }


}
plugins {
    id("com.android.application") version("8.7.2") apply false
    id("com.android.library") version("8.7.2") apply false
    id("com.google.dagger.hilt.android") version("2.51.1") apply false
    id("org.jetbrains.kotlin.android") version("2.0.21") apply false
    id("com.google.devtools.ksp") version ("2.0.21-1.0.25") apply false
}