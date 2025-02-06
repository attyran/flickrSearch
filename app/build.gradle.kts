import java.util.Properties
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.attyran.flickrsearch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.attyran.flickrsearch"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        rootProject.file("flickr.properties").inputStream().use { localProperties.load(it) }
        val flickrApiKey = localProperties["flickrApiKey"]
        buildConfigField("String", "FLICKR_API_KEY", "\"$flickrApiKey\"")
        val flickrApiSecretKey = localProperties["flickrApiSecret"]
        buildConfigField("String", "FLICKR_API_SECRET_KEY", "\"$flickrApiSecretKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val kotlinVersion:String by project

dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    val composeBom = platform("androidx.compose:compose-bom:2024.11.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui.tooling.preview)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.mockito.all)
    testImplementation(libs.mockito.core)
    implementation(libs.kotlin.stdlib.jdk7)
    testImplementation(libs.core.testing)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.kotlin.test)

    // Retrofit
    implementation(libs.retrofit2)
    implementation(libs.converter.moshi)
    implementation(libs.converter.scalars)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.okhttp3.logging.interceptor)



    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.okhttp)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Encrypted Shared Prefs
    implementation(libs.androidx.security.crypto)
}