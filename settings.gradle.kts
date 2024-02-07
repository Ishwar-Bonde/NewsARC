pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        jcenter(){
            content{
                includeModule("com.theartofdev.edmodo","android-image-cropper")
            }
        }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://maven.google.com")
        }
        jcenter(){
            content{
                includeModule("com.theartofdev.edmodo","android-image-cropper")
            }
        }

    }
}

rootProject.name = "TriMob"
include(":app")
 