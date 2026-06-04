plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}

val gCompileSdkVersion = providers.gradleProperty("gCompileSdkVersion").orNull?.toInt() ?: 0
val gMinSdkVersion = providers.gradleProperty("gMinSdkVersion").orNull?.toInt() ?: 0
val gTargetSdkVersion = providers.gradleProperty("gTargetSdkVersion").orNull?.toInt() ?: 0
val gVersionCode = providers.gradleProperty("gVersionCode").orNull?.toInt() ?: 0
val gVersionName = providers.gradleProperty("gVersionName").orNull ?: ""
val gKotlinVersion = providers.gradleProperty("gKotlinVersion").orNull ?: ""
val gKotlinCoroutineVersion = providers.gradleProperty("gKotlinCoroutineVersion").orNull ?: ""
val gKotlinSerializationVersion = providers.gradleProperty("gKotlinSerializationVersion").orNull ?: ""
val gRoomVersion = providers.gradleProperty("gRoomVersion").orNull ?: ""
val gAndroidKtxVersion = providers.gradleProperty("gAndroidKtxVersion").orNull ?: ""
val gMultiprocessPreferenceVersion = providers.gradleProperty("gMultiprocessPreferenceVersion").orNull ?: ""

android {
    namespace = "com.pclash.service"
    compileSdk = gCompileSdkVersion

    buildFeatures {
        aidl = true
    }

    sourceSets {
        getByName("main") {
            aidl.srcDirs("src/main/aidl")
        }
    }

    defaultConfig {
        minSdk = gMinSdkVersion
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
    ksp("androidx.room:room-compiler:$gRoomVersion")
    implementation(project(":core"))
    implementation(project(":common"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$gKotlinSerializationVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$gKotlinCoroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$gKotlinCoroutineVersion")
    implementation("androidx.room:room-runtime:$gRoomVersion")
    implementation("androidx.room:room-ktx:$gRoomVersion")
    implementation("androidx.sqlite:sqlite:2.4.0")
    implementation("androidx.core:core-ktx:$gAndroidKtxVersion")
    implementation("dev.rikka.rikkax.preference:multiprocess:1.0.0")
}
