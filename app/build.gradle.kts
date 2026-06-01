plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.wachtel.androidrecipesapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wachtel.androidrecipesapp"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

hilt {
    enableAggregatingTask = false
}


dependencies {
    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxLifecycleRuntimeKtx)
    implementation(libs.androidxActivityCompose)
    implementation(platform(libs.androidxComposeBom))
    implementation(libs.androidxComposeUi)
    implementation(libs.androidxComposeUiGraphics)
    implementation(libs.androidxComposeUiToolingPreview)
    implementation(libs.androidxComposeMaterial3)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.androidxEspressoCore)
    androidTestImplementation(platform(libs.androidxComposeBom))
    androidTestImplementation(libs.androidxComposeUiTestJunit4)
    debugImplementation(libs.androidxComposeUiTooling)
    debugImplementation(libs.androidxComposeUiTestManifest)
    implementation(libs.coilCompose)
    implementation(libs.androidxNavigationCompose)
    implementation(libs.androidxDatastorePreferences)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinxSerializationJson)
    implementation(libs.retrofit)
    implementation(libs.retrofitKotlinxSerializationConverter)
    implementation(libs.okhttpLoggingInterceptor)
    implementation(libs.androidxRoomRuntime)
    implementation(libs.androidxRoomKtx)
    ksp(libs.androidxRoomCompiler)
    implementation(libs.hiltAndroid)
    ksp(libs.hiltCompiler)
    implementation(libs.androidxHiltNavigationCompose)
}