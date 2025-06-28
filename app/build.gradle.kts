import org.gradle.kotlin.dsl.implementation

import java.io.FileInputStream
import java.util.Properties



plugins {

    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    //crashlytic
    id("com.google.firebase.crashlytics")

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
        //key openAI
//        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiKey\"")


    }
    android.sourceSets["main"].java.srcDirs("src/main/java", "src/main/kotlin")

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
    //gửi otp
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")

    //login by google

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.android.gms:play-services-auth:21.0.0")
    //firebase analytics
    implementation ("com.google.firebase:firebase-analytics:21.6.1")
    //firebase crashlytic
    implementation("com.google.firebase:firebase-crashlytics:18.6.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.3")

    //
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.5")
    implementation(libs.firebase.messaging.ktx)
    //roomdb
    val room_version = "2.7.1"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$room_version")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$room_version")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$room_version")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")
    ksp(libs.room.ksp)
    //lưu lịch sử tìm kiếm
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //API gemini
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    //luu lịch sử

    implementation("com.google.code.gson:gson:2.10.1")

//    implementation("androidx.compose.material3:material3:1.1.2")

    implementation("org.json:json:20240303")
    implementation("androidx.compose.material3:material3:1.2.1")

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

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.auth0.android:jwtdecode:2.0.2")

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
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")

    //Hiển thị video
    implementation("androidx.media3:media3-exoplayer:1.7.1")
    implementation("androidx.media3:media3-ui:1.7.1")

    implementation("androidx.compose.material:material:1.8.1")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("com.google.accompanist:accompanist-flowlayout:0.36.0")


    val nav_version = "2.9.0"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.guava:guava:32.1.2-jre")
}