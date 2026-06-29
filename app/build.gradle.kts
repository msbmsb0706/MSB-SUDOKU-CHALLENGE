plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.secrets) apply false
}

android {
  namespace = "com.example"
  compileSdk = 34

  lint {
    abortOnError = false
    checkReleaseBuilds = false
  }

  defaultConfig {
    applicationId = "com.example.sudoku"
    minSdk = 28
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val rawKeystoreBase64 = System.getenv("KEYSTORE_BASE64")
      
      if (!rawKeystoreBase64.isNullOrEmpty()) {
        val decryptedKeyFile = file("${layout.buildDirectory.get().asFile}/outputs/temp_signing_key.jks")
        decryptedKeyFile.parentFile.mkdirs()
        
        // Step 1: Strip structural text lines, headers, and spacing configurations
        var sanitizedBase64 = rawKeystoreBase64
            .replace("-", "")
            .replace("BEGIN EXTERNAL KEY", "")
            .replace("END EXTERNAL KEY", "")
            .replace("BEGIN PRIVATE KEY", "")
            .replace("END PRIVATE KEY", "")
            .replace("\\s".toRegex(), "")
            .trim()

        // Step 2: Auto-realign string groupings structurally to multiples of 4 bytes
        while (sanitizedBase64.length % 4 != 0) {
            sanitizedBase64 += "="
        }

        // Step 3: Parse utilizing a flexible MimeDecoder instance to bypass incorrect trailing bits
        val decoder = Class.forName("java.util.Base64").getMethod("getMimeDecoder").invoke(null)
        val decodedBytes = Class.forName("java.util.Base64\$Decoder").getMethod("decode", String::class.java).invoke(decoder, sanitizedBase64) as ByteArray
        decryptedKeyFile.writeBytes(decodedBytes)
        
        storeFile = decryptedKeyFile
        storePassword = System.getenv("KEYSTORE_PASSWORD")
        keyAlias = System.getenv("KEY_ALIAS")
        keyPassword = System.getenv("KEY_PASSWORD")
      } else {
        storeFile = file("${rootDir}/my-upload-key.jks")
        storePassword = System.getenv("STORE_PASSWORD")
        keyAlias = "upload"
        keyPassword = System.getenv("KEY_PASSWORD")
      }
    }
    
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

dependencies {
    // Standard Compose BOM & UI mappings
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
}
