plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.domichav.perfulandia"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.domichav.perfulandia"
        minSdk = 28
        targetSdk = 36
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // Coil - Para cargar imágenes desde URI
    implementation("io.coil-kt:coil-compose:2.5.0")
    // Version extendida de icons
    implementation("androidx.compose.material:material-icons-extended")


    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("androidx.navigation:navigation-compose:2.9.5")


    // ========================================
    // NETWORKING - API REST
    // ========================================

    // OkHttp - Cliente HTTP
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Retrofit - Cliente REST
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Coroutines (si no las tienes)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // DataStore - Para guardar tokens
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //rememberMultiplePermissionsState
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    // Coil - Para cargar imágenes desde URLs (opcional)
    //implementation("io.coil-kt:coil-compose:2.6.0")
}