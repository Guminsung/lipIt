plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)

}

android {
    namespace = "com.ssafy.lipit_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ssafy.lipit_app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        compose = true
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
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
    implementation(libs.gms.play.services.wearable)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.camera.view)
    implementation(libs.gms.play.services.wearable)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.test.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation("androidx.compose.material:material:1.7.5")     // material2 지원
    implementation("com.kizitonwose.calendar:compose:2.6.2")
    implementation("androidx.compose.material:material:1.7.5")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation ("androidx.navigation:navigation-compose:2.8.5")  // 네비게이션 구현

    implementation("androidx.compose.foundation:foundation:1.7.7") // LazyGrid 지원
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0") // 날짜 지원

    // retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // Compose ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // fcm
    implementation(platform("com.google.firebase:firebase-bom:33.8.0")) // 최신 버전 유지
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    // okhttp3
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // datastore
    implementation ("androidx.datastore:datastore-preferences:1.1.2")
    implementation ("androidx.datastore:datastore-core:1.1.2")  // 코어 의존성 (필요 시)

    // lottie
    implementation ("com.airbnb.android:lottie-compose:6.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation ("androidx.navigation:navigation-compose:2.4.0-alpha10") //네비게이션

    // datastore
    implementation ("androidx.datastore:datastore-preferences:1.1.2")
    implementation ("androidx.datastore:datastore-core:1.1.2")  // 코어 의존성 (필요 시)

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation ("androidx.navigation:navigation-compose:2.4.0-alpha10") //네비게이션
    implementation("androidx.compose.material:material:1.5.4")


    implementation("androidx.camera:camera-camera2:1.4.0-alpha02")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha02")
    implementation ("androidx.camera:camera-view:1.4.0-alpha02")

    // workmanager
    implementation ("androidx.work:work-runtime-ktx:2.7.1")

    // location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // indicator
    implementation("mx.platacard:compose-pager-indicator:0.0.8")

    implementation(kotlin("script-runtime"))

    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.compose.ui:ui-tooling:1.5.1") // Preview용

    // url을 통해 이미지를 불러오기 위한 이미지 라이브러리 - Coil
    implementation("io.coil-kt:coil-compose:2.4.0")

}