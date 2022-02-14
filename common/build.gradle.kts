import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.0.1"
    id("com.android.library")
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
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api("com.camackenzie:exvi-core:1.0-SNAPSHOT")
            }
        }
        val commonJvmAndroid = create("commonJvmAndroid") {
            dependsOn(commonMain)
        }
        val androidMain by getting {
            dependsOn(commonJvmAndroid)
            dependencies {
                api("androidx.appcompat:appcompat:1.2.0")
                api("androidx.core:core-ktx:1.3.1")
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
    compileSdkVersion(31)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(31)
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