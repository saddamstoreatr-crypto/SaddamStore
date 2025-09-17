plugins {
    alias(libs.plugins.android.dynamic.feature)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    // ❌ SafeArgs sirf :app module me hoga
}

android {
    namespace = "com.sdstore.feature_products"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    // ✅ Tests disable kar diye
    sourceSets {
        getByName("test").java.srcDirs("src/testDisabled")
        getByName("androidTest").java.srcDirs("src/androidTestDisabled")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature_cart"))

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Firebase (agar chahiye)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    // Glide
    implementation(libs.glide)
}
