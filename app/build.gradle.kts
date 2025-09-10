plugins {

    id("com.android.application")

    id("org.jetbrains.kotlin.android")

    id("com.google.devtools.ksp")

    id("com.google.dagger.hilt.android")

    id("com.google.gms.google-services")

    id("androidx.navigation.safeargs.kotlin")

    id("org.jetbrains.kotlin.plugin.parcelize")

}



android {

    namespace = "com.sdstore"

    compileSdk = 35



    defaultConfig {

        applicationId = "com.sdstore"

        minSdk = 24

        targetSdk = 35

        versionCode = 1

        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }



    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(

                getDefaultProguardFile("proguard-android-optimize.txt"),

                "proguard-rules.pro"

            )

            matchingFallbacks += listOf("release") // Fix for variant ambiguity

        }

        debug {

            matchingFallbacks += listOf("debug") // Fix for variant ambiguity

        }

    }



    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_17

        targetCompatibility = JavaVersion.VERSION_17

    }



    kotlinOptions {

        jvmTarget = "17"

    }



    buildFeatures {

        viewBinding = true

    }

}



dependencies {

// Project Modules

    implementation(project(mapOf("path" to ":core", "configuration" to "default")))

    implementation(project(":feature_auth"))

    implementation(project(":feature_products"))

    implementation(project(":feature_cart"))

    implementation(project(":feature_orders"))



// KSP processor for Hilt and Room

    ksp(project(mapOf("path" to ":core", "configuration" to "default")))



// AndroidX Libraries

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")

    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")



// Firebase

    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    implementation("com.google.firebase:firebase-config-ktx")

    implementation("com.google.firebase:firebase-messaging-ktx")

    implementation("com.google.firebase:firebase-appcheck-playintegrity")

    debugImplementation("com.google.firebase:firebase-appcheck-debug:18.0.0")



// Hilt

    implementation("com.google.dagger:hilt-android:2.51.1")

    ksp("com.google.dagger:hilt-compiler:2.51.1")

}