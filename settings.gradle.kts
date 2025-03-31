pluginManagement {
    includeBuild("compiler")
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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ai-chat"
include(":app")
include(":core:api")
include(":core:repository")
include(":core:model")
include(":core:common")
include(":feature:components")
include(":feature:chat")
include(":feature:login")
include(":feature:nav")
include(":feature:setting")
