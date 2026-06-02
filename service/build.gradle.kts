plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.kapt")
}

val gCompileSdkVersion: Int by project
val gMinSdkVersion: Int by project
val gTargetSdkVersion: Int by project
val gVersionCode: Int by project
val gVersionName: String by project
val gKotlinVersion: String by project
val gKotlinCoroutineVersion: String by project
val gKotlinSerializationVersion: String by project
val gRoomVersion: String by project
val gAndroidKtxVersion: String by project
val gMultiprocessPreferenceVersion: String by project

android {
    namespace = "com.pclash.service"
    compileSdk = gCompileSdkVersion

    defaultConfig {
        minSdk = gMinSdkVersion
        targetSdk = gTargetSdkVersion
        versionCode = gVersionCode
        versionName = gVersionName
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    kapt("androidx.room:room-compiler:$gRoomVersion")
    implementation(project(":core"))
    implementation(project(":common"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$gKotlinSerializationVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$gKotlinCoroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$gKotlinCoroutineVersion")
    implementation("androidx.room:room-runtime:$gRoomVersion")
    implementation("androidx.room:room-ktx:$gRoomVersion")
    implementation("androidx.core:core-ktx:$gAndroidKtxVersion")
    implementation("rikka.preference:multiprocesspreference:$gMultiprocessPreferenceVersion")
}
