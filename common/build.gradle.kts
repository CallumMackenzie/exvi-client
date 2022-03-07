val kormaVersion = "2.6.1"
val exviCoreVersion = "d1203f67ef92941eaf832576d36055a589b6caa8"
val mpSettingsVersion = "0.8.1"

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.4.31"
}

group = "com.camackenzie"
version = "1.0"

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    ios {
        binaries {
            framework()
        }
    }
    sourceSets {
        val commonMain by getting {
            resources.srcDirs("resources")
            dependencies {
                api("com.github.CallumMackenzie.exvi-core:exvi-core:$exviCoreVersion")
                api("com.russhwolf:multiplatform-settings:$mpSettingsVersion")
                api("com.russhwolf:multiplatform-settings-no-arg:$mpSettingsVersion")
                api("com.soywiz.korlibs.korma:korma:$kormaVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.2.0")
                api("androidx.core:core-ktx:1.3.1")
                api("io.ktor:ktor-client-android:1.6.7")
            }
        }
        val desktopMain by getting
    }
}

android {
    compileSdk = 31
    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("resources")
        }
    }
    defaultConfig {
        minSdk = 24
        targetSdk = 31
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
