plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.manajemenreportfinansialumkm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.manajemenreportfinansialumkm"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
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
    // ui
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.hdodenhof.circleimageview)

    // testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // navigation
    val nav_version = "2.8.9"
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // setup room
    val room_version = "2.6.1"

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)


    implementation(libs.glide)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // ViewModel
    val lifecycle_version = "2.8.7"
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)


    // firebase setup
    implementation(platform(libs.firebase.bom.v3281))

    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)


    implementation(libs.google.firebase.auth)

    // facebook
    implementation(libs.facebook.login)

    // google authentication 
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    val work_version = "2.9.1"
    implementation(libs.androidx.work.runtime.ktx)


}