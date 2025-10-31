plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}


android {
    namespace = "com.example.myhobbiesapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myhobbiesapp"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildFeatures {
        viewBinding = true
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.fragment:fragment-ktx:1.8.4")
    // ---- UI ----
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    // ---- NAVEGACIÓN (Para moverte entre Fragments) ----
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // ---- CICLO DE VIDA & HILOS (ViewModel, Coroutines) ----
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.coroutines.android) // Esta es tu librería de Corrutinas
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // ---- LIBRERÍAS DE RED (Para tu API REST) ----
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ---- LIBRERÍAS DE UI EXTRA (Animaciones y Tutorial) ----
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.3")

    // ---- TESTING (Pruebas) ----
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

