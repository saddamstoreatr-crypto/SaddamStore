plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.sdstore.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Common AndroidX libs that other modules will also need
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)

    // ✅ YEH LINE SPLASH SCREEN KA ERROR THEEK KAREGI
    api(libs.androidx.splashscreen)

    // Hilt for DI
    api(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Firebase
    api(platform(libs.firebase.bom))
    api(libs.firebase.auth)
    api(libs.firebase.firestore)
    api(libs.firebase.storage)
    api(libs.firebase.messaging)
    api(libs.firebase.database)

    // Room Database
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Retrofit for Networking
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)

    // Coroutines
    api(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.coroutines.playservices)

    // Glide for Image Loading
    api(libs.glide)
    ksp(libs.glide.compiler)

    // Shimmer for UI
    api(libs.shimmer)

    // Navigation
    api(libs.androidx.navigation.fragment.ktx)
    api(libs.androidx.navigation.ui.ktx)
    api(libs.androidx.fragment.ktx)
}