val accompanistVersion = "0.23.1"

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.1.0"
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
                api(project(":common"))
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.ui)
                api(compose.materialIconsExtended)
                api("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")
                api("com.google.accompanist:accompanist-placeholder:$accompanistVersion")
                api("com.google.accompanist:accompanist-insets:$accompanistVersion")
            }
        }
        val androidMain by getting
        val desktopMain by getting {
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

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
dependencies {
    api(compose.ui)
    api(compose.animationGraphics)
    api(compose.material)
    api(compose.material3)
    api(compose.foundation)
    api(compose.materialIconsExtended)
    api("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")
}