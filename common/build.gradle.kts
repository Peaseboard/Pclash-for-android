plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

// 数字类型 (Int) 必须加上 .toInt()
val gCompileSdkVersion = providers.gradleProperty("gCompileSdkVersion").orNull?.toInt() ?: 34
val gMinSdkVersion = providers.gradleProperty("gMinSdkVersion").orNull?.toInt() ?: 24
val gTargetSdkVersion = providers.gradleProperty("gTargetSdkVersion").orNull?.toInt() ?: 34

// 字符串类型 (String) 直接读取，注意后面的默认版本号要和你 gradle.properties 里的一致
val gKotlinVersion = providers.gradleProperty("gKotlinVersion").orNull ?: "1.9.22"
val gKotlinCoroutineVersion = providers.gradleProperty("gKotlinCoroutineVersion").orNull ?: "1.7.3"
val gKotlinSerializationVersion = providers.gradleProperty("gKotlinSerializationVersion").orNull ?: "1.6.0"
val gAndroidKtxVersion = providers.gradleProperty("gAndroidKtxVersion").orNull ?: "1.12.0"

android {
    namespace = "com.pclash.common"
    compileSdk = gCompileSdkVersion

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
    buildFeatures {
        buildConfig = true  // 👈 加上这一行
    }
}

dependencies {
    implementation("androidx.core:core-ktx:$gAndroidKtxVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$gKotlinCoroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$gKotlinSerializationVersion")
}
