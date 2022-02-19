import org.jetbrains.compose.compose

val mokoResourcesVersion = "0.18.0"
val mokoParcelizeVersion = "0.8.0"
val mokoGraphicsVersion = "0.9.0"

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.0.0"
    kotlin("plugin.serialization") version "1.4.31"
    id("dev.icerock.mobile.multiplatform-resources")
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
//                api("com.github.CallumMackenzie:exvi-core:eda62611ed")
                api("com.camackenzie:exvi-core:1.0-SNAPSHOT")
                api("dev.icerock.moko:resources:$mokoResourcesVersion")
                api("dev.icerock.moko:parcelize:$mokoParcelizeVersion")
                api("dev.icerock.moko:graphics:$mokoGraphicsVersion")
            }
        }
        val commonJvmAndroid = create("commonJvmAndroid") {
            dependsOn(commonMain)
            kotlin.srcDirs("src/commonJvmAndroid")
            dependencies {
                api("dev.icerock.moko:resources-compose:$mokoResourcesVersion")
            }
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
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
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

multiplatformResources {
    multiplatformResourcesPackage = "com.camackenzie.exvi"
}