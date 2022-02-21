import org.jetbrains.compose.compose

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.0.0"
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
    sourceSets {
        val commonMain by getting {
            resources.srcDirs("resources")
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api("com.github.CallumMackenzie.exvi-core:exvi-core:e211d1f72b")
            }
        }
        val commonJvmAndroid = create("commonJvmAndroid") {
            dependsOn(commonMain)
            kotlin.srcDirs("src/commonJvmAndroid")
        }
        val androidMain by getting {
            dependsOn(commonJvmAndroid)
            dependencies {
                api("androidx.appcompat:appcompat:1.2.0")
                api("androidx.core:core-ktx:1.3.1")
                api("io.ktor:ktor-client-android:1.6.7")
            }
        }
        val desktopMain by getting {
            dependsOn(commonJvmAndroid)
            dependencies {
                api(compose.preview)
            }
        }
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

dependencies {
    implementation("androidx.compose.ui:ui:1.2.0-alpha02")
    implementation("androidx.compose.ui:ui-text:1.2.0-alpha02")
    implementation("androidx.compose.material:material-icons-core:1.2.0-alpha02")
    implementation("androidx.compose.foundation:foundation:1.2.0-alpha02")
    implementation("androidx.compose.foundation:foundation-layout:1.2.0-alpha02")
}