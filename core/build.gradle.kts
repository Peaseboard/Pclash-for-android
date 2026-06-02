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

val geoipOutput = layout.buildDirectory.dir("outputs/geoip").get().asFile
val jniLibsDir = file("src/main/jniLibs")

android {
    namespace = "com.pclash.core"
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
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.core:core-ktx:$gAndroidKtxVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$gKotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$gKotlinCoroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$gKotlinSerializationVersion")
}

afterEvaluate {
    android.buildTypes.forEach {
        val cName = it.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
        tasks.named("package${cName}Assets").configure {
            dependsOn(tasks.named("downloadMihomoCore"))
            dependsOn(tasks.named("downloadGeoipDatabase"))
        }
    }
}
