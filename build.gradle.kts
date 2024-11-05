plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hiltCompiler) apply false
    alias(libs.plugins.navsafeargs) apply false
}

buildscript {
    dependencies {
        classpath(libs.okHttpClient)
        classpath (libs.gradle)
    }
}