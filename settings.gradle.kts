pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://raw.githubusercontent.com/RikkaApps/maven-repo/master/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven("https://raw.githubusercontent.com/RikkaApps/maven-repo/master/")
    }
}

rootProject.name = "Pclash"
include(":app")
include(":core")
include(":service")
include(":design")
include(":common")
