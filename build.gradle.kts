plugins {
    kotlin("multiplatform") version "2.1.21" apply false
    kotlin("jvm") version "2.1.21" apply false
    kotlin("plugin.serialization") version "2.1.21" apply false
    id("com.google.devtools.ksp") version "2.1.21-2.0.1" apply false
    id("de.jensklingenberg.ktorfit") version "2.5.1" apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "2.1.21"))
    }
}

group = "org.ktorbridge"
version = "0.2.2"