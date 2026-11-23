// Top-level build file where you can add configuration options common to all sub-projects/modules.
// KSP (Kotlin Symbol Processing) se declara a nivel de proyecto y se aplica en cada modulo.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
