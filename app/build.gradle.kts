plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "news.operational.NewsGO"
    compileSdk = 35

    defaultConfig {
        applicationId = "news.operational.NewsGO"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "2.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.activity:activity:1.10.1")
    implementation("com.google.firebase:firebase-auth:23.2.0")
    implementation("com.google.firebase:firebase-messaging:24.1.1")
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    annotationProcessor("androidx.room:room-compiler:2.7.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.KwabenBerko:News-API-Java:1.0.2")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.navigation:navigation-fragment:2.8.9")
    implementation("androidx.navigation:navigation-ui:2.8.9")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.firebase:firebase-auth:23.2.0")
    implementation("com.google.firebase:firebase-auth")
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.google.firebase:firebase-storage:21.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.facebook.android:facebook-android-sdk:latest.release")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.annotation:annotation:1.9.1")
    implementation("io.github.chaosleung:pinview:1.4.4")
    implementation("com.karumi:dexter:6.2.2")
//    implementation("com.canhub.android:image-cropper:4.3.2")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.MonsterTechnoGits:WebViewAdblock:1.1")
    implementation("com.hbb20:ccp:2.6.0")
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))

}