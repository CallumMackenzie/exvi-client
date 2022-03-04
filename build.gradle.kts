buildscript {
    val kotlinPluginVersion = "1.6.10"

    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinPluginVersion")
        classpath("com.android.tools.build:gradle:7.0.0")
    }
}

group = "com.camackenzie"
version = "1.0"

allprojects {

    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        mavenLocal()
    }
}