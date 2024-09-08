buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.52")
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.0.20")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")
    }


}
plugins {
    id("com.android.application") version("8.5.2") apply false
    id("com.android.library") version("8.5.2") apply false
    id("com.google.dagger.hilt.android") version("2.44") apply false
    id("org.jetbrains.kotlin.android") version("2.0.20") apply false
    id("com.google.devtools.ksp") version ("2.0.20-1.0.25") apply false
}