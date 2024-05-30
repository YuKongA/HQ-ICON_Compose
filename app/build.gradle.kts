import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.io.ByteArrayOutputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "top.yukonga.hq_icon"
    compileSdk = 34

    defaultConfig {
        applicationId = namespace
        minSdk = 26
        targetSdk = 34
        versionCode = getVersionCode()
        versionName = "1.0" + "-" + getVersionName()

        vectorDrawables {
            useSupportLibrary = true
        }
    }
    val properties = Properties()
    runCatching { properties.load(project.rootProject.file("local.properties").inputStream()) }
    val keystorePath = properties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")
    val keystorePwd = properties.getProperty("KEYSTORE_PASS") ?: System.getenv("KEYSTORE_PASS")
    val alias = properties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
    val pwd = properties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
    if (keystorePath != null) {
        signingConfigs {
            register("github") {
                storeFile = file(keystorePath)
                storePassword = keystorePwd
                keyAlias = alias
                keyPassword = pwd
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    } else {
        signingConfigs {
            register("release") {
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig =
                signingConfigs.getByName(if (keystorePath != null) "github" else "release")
        }
        debug {
            if (keystorePath != null) signingConfig = signingConfigs.getByName("github")
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf(
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions"
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "**"
        }
        applicationVariants.all {
            outputs.all {
                (this as BaseVariantOutputImpl).outputFileName =
                    "HQ_ICON-$versionName($versionCode)-$name.apk"
            }
        }
    }
}

fun getGitCommitCount(): Int {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-list", "--count", "HEAD")
        standardOutput = out
    }
    return out.toString().trim().toInt()
}

fun getGitDescribe(): String {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--always")
        standardOutput = out
    }
    return out.toString().trim()
}

fun getVersionCode(): Int {
    val commitCount = getGitCommitCount()
    return commitCount
}

fun getVersionName(): String {
    return getGitDescribe()
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.material.icons.extended.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    debugImplementation(libs.androidx.ui.tooling)
}