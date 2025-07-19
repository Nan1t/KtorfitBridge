plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("de.jensklingenberg.ktorfit")
}

repositories {
    mavenCentral()
}

dependencies {
    ksp(project(":generator"))
    implementation(project(":library"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:2.5.1")
    implementation("io.ktor:ktor-server-core:3.2.0")
    implementation("io.ktor:ktor-server-netty:3.2.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

ksp {
    arg("bridgeBasePackage", "org.ktorbridge.api")
    arg("bridgeOverridePackage", "com.test.proto")
}