plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    compileSdk = 32
    buildToolsVersion = "32.0.0"

    defaultConfig {
        applicationId = "com.onfleet.sdk.onfleetclientexample"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "0.10.3"
        multiDexEnabled = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        debug {
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0-beta02"
    }
    namespace = "com.onfleet.sdk.onfleetclientexample"
}

dependencies {
    implementation("com.onfleet:driver:0.10.3")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("androidx.exifinterface:exifinterface:1.3.3")
    implementation("com.google.firebase:firebase-core:21.0.0")
    implementation("com.google.firebase:firebase-messaging:23.0.5")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2")
    implementation("androidx.compose.ui:ui:1.2.0-beta03")
    implementation("androidx.compose.ui:ui-tooling:1.2.0-beta03")
    implementation("androidx.compose.foundation:foundation:1.2.0-beta03")
    implementation("androidx.compose.compiler:compiler:1.2.0-beta03")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    implementation("androidx.compose.material3:material3:1.0.0-alpha13")
    implementation("com.google.accompanist:accompanist-permissions:0.24.10-beta")
}