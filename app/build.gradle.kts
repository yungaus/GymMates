plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // INI SOLUSINYA: Plugin ini WAJIB ada di Kotlin 2.0+
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.gm" // Sesuaikan package kamu
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gm"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    // PERHATIAN: Di Kotlin 2.0, blok "composeOptions" yang isinya "kotlinCompilerExtensionVersion"
    // SUDAH TIDAK DIPERLUKAN LAGI (malah bikin error). Jadi saya hapus.

    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // --- Library Penghubung (Wajib) ---
    implementation("androidx.activity:activity-compose:1.9.0")

    // --- Library Tampilan ---
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Ikon-ikon
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // Buat liat preview di Android Studio
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.navigation:navigation-compose:2.7.7")
}