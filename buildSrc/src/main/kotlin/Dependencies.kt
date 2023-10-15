import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {
    const val core = "androidx.core:core-ktx:${Versions.kotlin}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val constraintslayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintslayout}"
    const val junit = "junit:junit:${Versions.junit}"
    const val test_junit = "androidx.test.ext:junit:${Versions.test_junit}"
    const val expresso = "androidx.test.espresso:espresso-core:${Versions.expresso}"
    const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"

    const val composeMaterial = "androidx.compose.material3:material3:${Versions.composeMaterial3}"
    const val composeUi = "androidx.compose.ui:ui:${Versions.compose}"
    const val composeUiGraphics = "androidx.compose.ui:ui-graphics:${Versions.compose}"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Versions.compose}"
    const val composeRuntime = "androidx.compose.runtime:runtime:${Versions.compose}"

    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    const val hiltAgp = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"

    const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
    const val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val jsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val rxJava = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val moshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"

    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"


    const val splash = "androidx.core:core-splashscreen:${Versions.splash}"

    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"

    const val lifecycleViewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle_viewmodel}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle_viewmodel}"
    const val lifecycleLivedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle_viewmodel}"

    const val navigationFrag = "androidx.navigation:navigation-fragment-ktx:${Versions.nav_fragment}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.nav_fragment}"

    const val exoPlayer = "com.google.android.exoplayer:exoplayer:${Versions.exoPlayer}"
    const val exoPlayerCore = "com.google.android.exoplayer:exoplayer-core:${Versions.exoPlayer}"
    const val exoPlayerDash = "com.google.android.exoplayer:exoplayer-dash:${Versions.exoPlayer}"
    const val exoPlayerUi = "com.google.android.exoplayer:exoplayer-ui:${Versions.exoPlayer}"

    const val circlularImage = "de.hdodenhof:circleimageview:${Versions.circularImage}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
}

fun DependencyHandler.core() {
    implementation(Dependencies.core)
}

fun DependencyHandler.appcompat() {
    implementation(Dependencies.appcompat)
}

fun DependencyHandler.material() {
    implementation(Dependencies.material)
}

fun DependencyHandler.constraintslayout() {
    implementation(Dependencies.constraintslayout)
}

fun DependencyHandler.junit() {
    testImplementation(Dependencies.junit)
}

fun DependencyHandler.test_junit() {
    androidTestImplementation(Dependencies.test_junit)
}

fun DependencyHandler.expresso() {
    androidTestImplementation(Dependencies.expresso)
}

fun DependencyHandler.recyclerview() {
    implementation(Dependencies.recyclerview)
}

fun DependencyHandler.splash() {
    implementation(Dependencies.splash)
}

fun DependencyHandler.coroutines() {
    implementation(Dependencies.coroutinesAndroid)
    implementation(Dependencies.coroutinesCore)
}

fun DependencyHandler.lifecycle() {
    implementation(Dependencies.lifecycleViewmodel)
    implementation(Dependencies.lifecycleRuntime)
    implementation(Dependencies.lifecycleLivedata)
}

fun DependencyHandler.navigation() {
    implementation(Dependencies.navigationFrag)
    implementation(Dependencies.navigationUi)
}

fun DependencyHandler.room() {
    implementation(Dependencies.roomRuntime)
    implementation(Dependencies.roomKtx)
    kapt(Dependencies.roomCompiler)
}

fun DependencyHandler.retrofit() {
    implementation(Dependencies.retrofit)
    implementation(Dependencies.moshiConverter)
    implementation(Dependencies.jsonConverter)
    implementation(Dependencies.rxJava)
    implementation(Dependencies.okHttp)
    implementation(Dependencies.okHttpLoggingInterceptor)
}

fun DependencyHandler.compose() {
    implementation(Dependencies.composeUi)
    implementation(Dependencies.composeRuntime)
    implementation(Dependencies.composeUiGraphics)
    implementation(Dependencies.composeUiTooling)
    implementation(Dependencies.composeMaterial)
    debugImplementation(Dependencies.composeUiToolingPreview)
}

fun DependencyHandler.hilt() {
    implementation(Dependencies.hiltAndroid)
    kapt(Dependencies.hiltCompiler)
}

fun DependencyHandler.exoplayer() {
    implementation(Dependencies.exoPlayer)
    implementation(Dependencies.exoPlayerCore)
    implementation(Dependencies.exoPlayerDash)
    implementation(Dependencies.exoPlayerUi)
}

fun DependencyHandler.circularImage() {
    implementation(Dependencies.circlularImage)
}

fun DependencyHandler.glide() {
    implementation(Dependencies.glide)
}