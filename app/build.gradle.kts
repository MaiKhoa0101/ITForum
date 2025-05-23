import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"

}

android {
    namespace = "com.example.itforum"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.itforum"
        minSdk = 26
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // ViewModel + LiveData (bắt buộc)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")  // ViewModel (Kotlin)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")   // LiveData (Kotlin)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")    // LifecycleScope
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")


    // Jetpack Compose tích hợp với ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Coroutines (nếu dùng Flow)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    //Tải và hiển thị hình ảnh online
    implementation(platform("androidx.compose:compose-bom:2025.05.00"))
    implementation("io.coil-kt:coil-compose:2.6.0")


    implementation("androidx.compose.material:material:1.8.1")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("com.google.accompanist:accompanist-flowlayout:0.36.0")

    implementation("io.coil-kt.coil3:coil-compose:3.2.0")

    val nav_version = "2.9.0"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")
}