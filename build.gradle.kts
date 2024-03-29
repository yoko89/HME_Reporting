buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.21")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    }


}
plugins {
    id("com.android.application") version("8.2.2") apply false
    id("com.android.library") version("8.2.2") apply false
    id("com.google.dagger.hilt.android") version("2.44") apply false
    id("org.jetbrains.kotlin.android") version("1.9.22") apply false
    id("com.google.devtools.ksp") version ("1.9.22-1.0.17") apply false
}

tasks.register("clean",Delete::class) {
    delete(rootProject.buildDir)
}