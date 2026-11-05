import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "rocks.gorjan.gokixp"
    compileSdk = 36

    defaultConfig {
        // Personal fork: a different id so it installs next to the original (issue: coexist, own signing key)
        applicationId = "jp.personal.winlauncher"
        minSdk = 29
        targetSdk = 36
        versionCode = 29
        versionName = "2.1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Release signing for this personal fork. The key and its passwords live outside the repository
    // (default ~/.config/ai-r-sys/keystores/windows-launcher.signing.properties, or WL_SIGNING_PROPS);
    // without that file the release build is simply left unsigned.
    val signingProps = Properties().apply {
        val f = file(System.getenv("WL_SIGNING_PROPS") ?: "${System.getProperty("user.home")}/.config/ai-r-sys/keystores/windows-launcher.signing.properties")
        if (f.exists()) f.inputStream().use { load(it) }
    }
    signingConfigs {
        if (signingProps.getProperty("storeFile") != null) {
            create("personal") {
                storeFile = file(signingProps.getProperty("storeFile"))
                storePassword = signingProps.getProperty("storePassword")
                keyAlias = signingProps.getProperty("keyAlias")
                keyPassword = signingProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            signingConfigs.findByName("personal")?.let { signingConfig = it }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Kotlin jvmTarget follows compileOptions.targetCompatibility (AGP built-in Kotlin)
    buildFeatures {
        viewBinding = true
    }
    testOptions {
        unitTests {
            // The Briefcase's wire code logs; a unit test should not have to care.
            isReturnDefaultValues = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.29")

    // WindowManager for foldable device detection
    implementation("androidx.window:window:1.3.0")

    // OSMDroid for OpenStreetMap

    // PDF rendering with PdfBox
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    // Google Drive API
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20240123-2.0.0") {
        exclude(group = "org.apache.httpcomponents")
        exclude(group = "org.apache.httpcomponents", module = "httpclient")
    }
    implementation("com.google.api-client:google-api-client-android:2.2.0") {
        exclude(group = "org.apache.httpcomponents")
    }
    implementation("com.google.http-client:google-http-client-gson:1.43.3") {
        exclude(group = "org.apache.httpcomponents")
    }
    implementation("com.google.http-client:google-http-client-android:1.43.3") {
        exclude(group = "org.apache.httpcomponents")
    }

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}