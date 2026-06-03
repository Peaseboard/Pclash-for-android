plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

import java.util.Locale

// Int 类型：使用 .toInt() 转换，并设置默认值以防读取失败
val gCompileSdkVersion = providers.gradleProperty("gCompileSdkVersion").orNull?.toInt() ?: 34
val gMinSdkVersion = providers.gradleProperty("gMinSdkVersion").orNull?.toInt() ?: 24
val gTargetSdkVersion = providers.gradleProperty("gTargetSdkVersion").orNull?.toInt() ?: 34

// String 类型：使用 orNull 安全获取，如果不存在则使用空字符串或默认值
val gKotlinVersion = providers.gradleProperty("gKotlinVersion").orNull ?: "1.9.22"
val gKotlinCoroutineVersion = providers.gradleProperty("gKotlinCoroutineVersion").orNull ?: "1.7.3"
val gKotlinSerializationVersion = providers.gradleProperty("gKotlinSerializationVersion").orNull ?: "1.6.2"
val gAndroidKtxVersion = providers.gradleProperty("gAndroidKtxVersion").orNull ?: "1.12.0"
val gVersionName = providers.gradleProperty("gVersionName").orNull ?: "1.0.0"

val geoipOutput = layout.buildDirectory.dir("outputs/geoip").get().asFile
val jniLibsDir = file("src/main/jniLibs")

android {
    namespace = "com.pclash.core"
    compileSdk = gCompileSdkVersion

    defaultConfig {
        minSdk = gMinSdkVersion
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "VERSION_NAME", "\"${gVersionName}\")
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

    sourceSets {
        named("main") {
            assets.srcDir(geoipOutput)
            jniLibs.srcDir(jniLibsDir)
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
        buildConfig = true
    }
}

    // Deprecated fix: moved out of defaultConfig

dependencies {
    implementation(project(":common"))
    implementation("androidx.core:core-ktx:$gAndroidKtxVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$gKotlinCoroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$gKotlinSerializationVersion")
}

apply(from = "mihomo.gradle.kts")

afterEvaluate {
    android.buildTypes.forEach {
        val cName = it.name.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString() }
        tasks.named("package${cName}Assets").configure {
            dependsOn(tasks.named("downloadMihomoCore"))
            dependsOn(tasks.named("downloadGeoipDatabase"))
        }
    }
}
