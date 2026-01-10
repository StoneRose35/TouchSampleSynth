import ch.sr35.touchsamplesynth.build.InstrumentBuilderTask
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.config.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ch.sr35.touchsamplesynth"
    compileSdk = 36

    defaultConfig {
        applicationId = "ch.sr35.touchsamplesynth"
        minSdk = 29
        targetSdk = 35
        versionCode = 1002
        versionName = "1.10.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isJniDebuggable = true
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.github.tutorialsandroid:filepicker:9.2.5")
    implementation("codes.side:andcolorpicker:0.6.2")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.7.0")
    implementation(kotlin("reflect"))
}

tasks.register<InstrumentBuilderTask>("instrumentBuilder") {
}

tasks.preBuild.dependsOn(tasks.named("instrumentBuilder"))