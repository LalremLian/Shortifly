plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id ("kotlin-android")
    kotlin("kapt")
}

android {
    namespace = "com.lazydeveloper.shortifly"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lazydeveloper.shortifly"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
        dataBinding = true
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
    hilt {
        enableAggregatingTask = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")
    junit()
    test_junit()
    expresso()

    core()
    appcompat()
    material()
    constraintslayout()
    recyclerview()

    lifecycle()
    navigation()
    retrofit()
    hilt()
    coroutines()
    exoplayer()
    circularImage()
    glide()
}