pluginManagement {
    repositories {
        google()
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

rootProject.name = "SaddamStore"
include(":app")
include(":core")
include(":feature_auth")
include(":feature_cart")
include(":feature_orders")
include(":feature_products")