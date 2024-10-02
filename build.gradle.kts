// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")  // Updated Google services plugin
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
}
