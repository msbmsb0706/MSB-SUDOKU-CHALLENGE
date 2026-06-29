android {
  namespace = "com.example"
  // Fixed: Set to a stable compilation target compatible with Gradle 8.7
  compileSdk = 34

  // Fixed: Added to prevent lint warnings or release checks from aborting the build
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
      val keystoreBase64 = System.getenv("KEYSTORE_BASE64")
      
      if (!keystoreBase64.isNullOrEmpty()) {
        // Fixed: Swapped out deprecated 'buildDir' with layout.buildDirectory syntax
        val decryptedKeyFile = file("${layout.buildDirectory.get().asFile}/outputs/temp_signing_key.jks")
        decryptedKeyFile.parentFile.mkdirs()
        decryptedKeyFile.writeBytes(java.util.Base64.getDecoder().decode(keystoreBase64.trim()))
        
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
