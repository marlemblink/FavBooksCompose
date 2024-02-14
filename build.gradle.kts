// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    //Firebase and google
    id("com.google.gms.google-services") version "4.4.0" apply false
    //di
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
}