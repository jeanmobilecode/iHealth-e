plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.logicalayer.ihealthe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.logicalayer.ihealthe"
        minSdk = 24
        targetSdk = 35
        versionCode = 8
        versionName = "1.0.8"

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
}

dependencies {
    //Autenticacao Firestore
    implementation ("com.google.firebase:firebase-auth-ktx:22.1.0")


    //AdMob lite
    implementation ("com.google.android.gms:play-services-ads-lite:22.6.0")

    //Animacoes JSON
    implementation ("com.airbnb.android:lottie:6.1.0")

    //Arquitetura MVVM
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation ("androidx.activity:activity-ktx:1.8.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    //SplashScreen do android
    implementation ("androidx.core:core-splashscreen:1.0.1")

    //GSON
    implementation("com.google.code.gson:gson:2.10.1")

    //Coroutines (assincronismo)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")

    //UI
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs .junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
