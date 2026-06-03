import org.apache.tools.ant.taskdefs.condition.Os
import java.io.*
import java.util.*
import java.net.*
import java.time.*

val geoipDatabaseUrl = "https://github.com/MetaCubeX/meta-rules-dat/releases/download/latest/geoip.metadb"
val geoipInvalidate = Duration.ofDays(7)
val geoipOutput = layout.buildDirectory.dir("outputs/geoip").get().asFile
val mihomoVersion = "v1.18.10"
val mihomoBaseUrl = "https://github.com/MetaCubeX/mihomo/releases/download/$mihomoVersion/"

val String.exe: String
    get() = if (Os.isFamily(Os.FAMILY_WINDOWS)) "$this.exe" else this

tasks.register("downloadMihomoCore") {
    doLast {
        val jniLibsDir = file("src/main/jniLibs")

        val mapping = mapOf(
            "armeabi-v7a" to "mihomo-android-armv7-v1.18.10.gz",
            "arm64-v8a" to "mihomo-android-arm64-v1.18.10.gz",
            "x86" to "mihomo-android-386-v1.18.10.gz",
            "x86_64" to "mihomo-android-amd64-v1.18.10.gz"
        )

        mapping.forEach { (abi, fileName) ->
            val libDir = jniLibsDir.resolve(abi)
            val outputFile = libDir.resolve("libmihomo.so")
            val gzFile = layout.buildDirectory.get().asFile.resolve("downloads/$fileName")

            if (outputFile.exists()) return@forEach

            println("Downloading $fileName ...")
            libDir.mkdirs()
            gzFile.parentFile.mkdirs()

            val url = URL("$mihomoBaseUrl$fileName")
            url.openStream().use { input ->
                FileOutputStream(gzFile).use { output ->
                    input.copyTo(output)
                }
            }

            println("Extracting $fileName ...")
            exec {
                workingDir = layout.buildDirectory.get().asFile
                commandLine("gunzip", "-f", gzFile.absolutePath)
            }

            // The extracted file is usually named 'mihomo' or similar, rename it
            val extractedFile = gzFile.resolveSibling(fileName.replace(".gz", ""))
            if (!extractedFile.exists()) {
                val files = gzFile.parentFile.listFiles { f -> f.name.startsWith("mihomo-android") && f.extension != "gz" }
                if (files != null && files.isNotEmpty()) {
                    files[0].renameTo(outputFile)
                }
            } else {
                extractedFile.renameTo(outputFile)
            }

            exec {
                workingDir = layout.buildDirectory.get().asFile
                commandLine("chmod", "+x", outputFile.absolutePath)
            }
        }
    }
}

tasks.register("downloadGeoipDatabase") {
    val geoipFile = geoipOutput.resolve("geoip.metadb")

    onlyIf {
        !geoipFile.exists() || System.currentTimeMillis() - geoipFile.lastModified() > geoipInvalidate.toMillis()
    }

    doLast {
        geoipOutput.mkdirs()
        URL(geoipDatabaseUrl).openConnection().getInputStream().use { input ->
            FileOutputStream(geoipFile).use { output ->
                input.copyTo(output)
            }
        }
    }
}
