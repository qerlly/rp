plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.google.services)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

android {
    namespace = "pl.agora.radiopogoda"
    compileSdk = 35

    defaultConfig {
        applicationId = "pl.agora.radiopogoda"
        minSdk = 26
        targetSdk = 35
        versionCode = 256
        versionName = "2.6.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "1.8" }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    packagingOptions { resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}") }


    configurations {
        implementation {
            exclude(group = "org.jetbrains", module = "annotations")
        }
    }

    room { schemaDirectory("$projectDir/schemas") }
}

dependencies {
    implementation(files("libs/GemiusSDK_2.1.2.aar"))

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.navigation.runtime.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.runtime.compose)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.work.runtime.ktx)
    implementation(libs.android.joda)
    implementation(libs.datastore.preferences)
    implementation(libs.kotlin.stdlib)

    implementation(libs.consent.library)
    implementation(libs.play.services.analytics)
    implementation(libs.play.services.ads)
    implementation(libs.user.messaging.platform)

    implementation(libs.ui)
    implementation(libs.material)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.google.material)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.androidx.core.splashscreen.v100beta02)

    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.landscapist.coil)
    implementation(libs.landscapist.palette)
    implementation(libs.landscapist.animation)

    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)

    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer.dash)

    implementation(libs.glide)
    ksp(libs.glide.ksp)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.messaging.ktx)

    implementation(libs.androidx.multidex)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.media)
    implementation(libs.interactivemedia)
}