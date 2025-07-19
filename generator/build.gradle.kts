plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":library"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.1.21-2.0.1")
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:2.5.1")
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("com.squareup:kotlinpoet-ksp:2.2.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}