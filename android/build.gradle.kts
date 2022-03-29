plugins {
    id("org.jetbrains.compose") version "1.1.1"
    id("com.android.application")
    kotlin("android")
}

group = "com.camackenzie"
version = "1.0"

repositories {
    jcenter()
}

dependencies {
    implementation(project(":compose"))
    implementation("androidx.activity:activity-compose:1.3.0")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.camackenzie.android"
        minSdk = 24
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}