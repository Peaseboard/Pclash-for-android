import java.util.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

// 使用 providers.gradleProperty 替代 by project，解决类型转换报错
val gCompileSdkVersion = providers.gradleProperty("gCompileSdkVersion").orNull?.toInt() ?: 34
val gMinSdkVersion = providers.gradleProperty("gMinSdkVersion").orNull?.toInt() ?: 24
val gTargetSdkVersion = providers.gradleProperty("gTargetSdkVersion").orNull?.toInt() ?: 34
val gVersionCode = providers.gradleProperty("gVersionCode").orNull?.toInt() ?: 1
val gVersionName = providers.gradleProperty("gVersionName").orNull ?: "1.0.0"
val gKotlinVersion = providers.gradleProperty("gKotlinVersion").orNull ?: "1.9.22"
val gKotlinCoroutineVersion = providers.gradleProperty("gKotlinCoroutineVersion").orNull ?: "1.7.3"
val gAndroidKtxVersion = providers.gradleProperty("gAndroidKtxVersion").orNull ?: "1.12.0"
val gRecyclerviewVersion = providers.gradleProperty("gRecyclerviewVersion").orNull ?: "1.3.2"
val gAppCompatVersion = providers.gradleProperty("gAppCompatVersion").orNull ?: "1.6.1"
val gMaterialDesignVersion = providers.gradleProperty("gMaterialDesignVersion").orNull ?: "1.11.0"
val gShizukuPreferenceVersion = providers.gradleProperty("gShizukuPreferenceVersion").orNull ?: "10.0.0"
val gMultiprocessPreferenceVersion = providers.gradleProperty("gMultiprocessPreferenceVersion").orNull ?: "1.1.0"

android {
    namespace = "com.pclash"
    compileSdk = gCompileSdkVersion

    defaultConfig {
        applicationId = "com.pclash"
        minSdk = gMinSdkVersion
        targetSdk = gTargetSdkVersion
        versionCode = gVersionCode
        versionName = gVersionName
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
        viewBinding = true
        buildConfig = true
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    // For AAB (Google Play)
    bundle {
        language {
            enableSplit = false
        }
        density {
            enableSplit = false
        }
        abi {
            enableSplit = true
        }
    }

    val signingFile = rootProject.file("keystore.properties")
    if (signingFile.exists()) {
        val properties = Properties().apply {
            signingFile.inputStream().use { load(it) }
        }
        signingConfigs {
            create("release") {
                storeFile = rootProject.file(properties.getProperty("storeFile"))
                storePassword = properties.getProperty("storePassword")
                keyAlias = properties.getProperty("keyAlias")
                keyPassword = properties.getProperty("keyPassword")
            }
        }
        buildTypes {
            release {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
}

// Generate PACKAGE_NAME_BASE64 at configuration time
val packageNameBase64 = Base64.getEncoder().encodeToString("com.pclash".toByteArray(Charsets.UTF_8))
android.buildTypes.forEach {
    it.buildConfigField("String", "PACKAGE_NAME_BASE64", "\"$packageNameBase64\"")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":service"))
    implementation(project(":design"))
    implementation(project(":common"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$gKotlinCoroutineVersion")
    implementation("androidx.recyclerview:recyclerview:$gRecyclerviewVersion")
    implementation("androidx.core:core-ktx:$gAndroidKtxVersion")
    implementation("androidx.appcompat:appcompat:$gAppCompatVersion")
    implementation("com.google.android.material:material:$gMaterialDesignVersion")

    implementation("androidx.preference:preference-ktx:1.2.1")
}
