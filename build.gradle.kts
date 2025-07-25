// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

import java.util.Properties

val mapkitApiKey: String by extra {
    val properties = Properties()
    file("local.properties").inputStream().use { properties.load(it) }
    properties.getProperty("MAPKIT_API_KEY", "")
}