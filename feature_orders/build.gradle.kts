plugins {
    alias(libs.plugins.android.dynamic.feature)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.sdstore.feature_orders"
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
    implementation(project(":app"))
    implementation(project(":core"))

    // Navigation and Fragment KTX
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Hilt for DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ✅ YEH DEPENDENCIES KSP ERROR THEEK KARENGI
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

// Glide
implementation(libs.glide)
}