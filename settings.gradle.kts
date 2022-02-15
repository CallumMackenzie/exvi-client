pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
    
}
rootProject.name = "exvi-client"


include(":android")
include(":desktop")
include(":js")
include(":common")

