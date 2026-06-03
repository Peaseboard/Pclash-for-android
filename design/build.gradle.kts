plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

// Int 类型 (添加 .toInt() 防止崩溃)
val gCompileSdkVersion = providers.gradleProperty("gCompileSdkVersion").orNull?.toInt() ?: 34
val gMinSdkVersion = providers.gradleProperty("gMinSdkVersion").orNull?.toInt() ?: 24
val gTargetSdkVersion = providers.gradleProperty("gTargetSdkVersion").orNull?.toInt() ?: 34
val gVersionCode = providers.gradleProperty("gVersionCode").orNull?.toInt() ?: 1

// String 类型 (使用 orNull 安全获取)
val gVersionName = providers.gradleProperty("gVersionName").orNull ?: "1.0"
val gKotlinVersion = providers.gradleProperty("gKotlinVersion").orNull ?: "1.9.22"
val gAndroidKtxVersion = providers.gradleProperty("gAndroidKtxVersion").orNull ?: "1.12.0"
val gAppCompatVersion = providers.gradleProperty("gAppCompatVersion").orNull ?: "1.6.1"
val gMaterialDesignVersion = providers.gradleProperty("gMaterialDesignVersion").orNull ?: "1.11.0"

android {
    namespace = "com.pclash.design"
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
}

    // Deprecated fix: moved out of defaultConfig

dependencies {
    implementation(project(":common"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("androidx.appcompat:appcompat:$gAppCompatVersion")
    implementation("androidx.core:core-ktx:$gAndroidKtxVersion")
    implementation("com.google.android.material:material:$gMaterialDesignVersion")
}
