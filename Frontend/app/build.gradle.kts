
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.dietiestates"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dietiestates"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
dependencies {
    val nav_version = "2.7.4"
    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.00"))


    // Jetpack Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-text-google-fonts")
    implementation("com.google.accompanist:accompanist-pager:0.33.2-alpha")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.33.2-alpha")
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    // JWT DECODE
    implementation ("com.auth0.android:jwtdecode:2.0.1")


    // Navigation
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Lifecycle / ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Core / Activity
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.core:core-ktx:1.12.0")

    // Retrofit + Coroutine
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}