rootProject.name = "PhotoBackup"
include(":app")

pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.google.com/")
        }
        maven {
            url = uri("https://dl.google.com/dl/android/maven2/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://maven.google.com/")
        }
        maven {
            url = uri("https://dl.google.com/dl/android/maven2/")
        }
        mavenCentral()
    }
}
