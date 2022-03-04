import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.1.0"
}

group = "com.camackenzie"
version = "1.0.0"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":compose"))
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.camackenzie.exvi.client.desktop.MainKt"

        nativeDistributions {
            packageName = "Exvi Fitness"
            copyright = "© 2022 Callum Mackenzie. All rights reserved."
            vendor = "com.camackenzie"

            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            macOS {
                iconFile.set(project.file("logo.icns"))
            }
            windows {
                iconFile.set(project.file("logo.ico"))
                perUserInstall = true
                dirChooser = true
            }
            linux {
                iconFile.set(project.file("logo.png"))
            }
        }
    }
}