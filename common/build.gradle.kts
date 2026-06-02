plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val gCompileSdkVersion: Int by project
val gMinSdkVersion: Int by project
val gTargetSdkVersion: Int by project
val gVersionCode: Int by project
val gVersionName: String by project
val gKotlinVersion: String by project
val gKotlinCoroutineVersion: String by project
val gKotlinSerializationVersion: String by project
val gAndroidKtxVersion: String by project

android {
    namespace = "com.pclash.common"
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
    implementation("androidx.core:core-ktx:$gAndroidKtxVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$gKotlinCoroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$gKotlinSerializationVersion")
}
