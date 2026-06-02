import java.util.*

plugins {
    id("com.android.application")
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
val gAndroidKtxVersion: String by project
val gRecyclerviewVersion: String by project
val gAppCompatVersion: String by project
val gMaterialDesignVersion: String by project
val gShizukuPreferenceVersion: String by project
val gMultiprocessPreferenceVersion: String by project

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
    implementation("moe.shizuku.preference:preference-appcompat:$gShizukuPreferenceVersion")
    implementation("moe.shizuku.preference:preference-simplemenu-appcompat:$gShizukuPreferenceVersion")
}

tasks.register("injectPackageNameBase64") {
    doFirst {
        val packageName = "com.pclash"
        val base64 = Base64.getEncoder().encodeToString(packageName.toByteArray(Charsets.UTF_8))
        android.buildTypes.forEach {
            it.buildConfigField("String", "PACKAGE_NAME_BASE64", "\"$base64\"")
        }
    }
}

tasks.named("preBuild") {
    dependsOn("injectPackageNameBase64")
}
