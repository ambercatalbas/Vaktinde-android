pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Vaktinde"

include(":app")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":core:common")
include(":feature:home")
include(":feature:qibla")
include(":feature:calendar")
include(":feature:settings")
include(":feature:onboarding")
include(":widget")
