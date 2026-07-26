plugins {
    kotlin("plugin.serialization") version "2.1.21"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${project.property("kotlinVersion")}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:${property("kotlinxSerializationVersion")}")
}
