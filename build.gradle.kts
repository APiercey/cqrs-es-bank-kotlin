import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"

    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.eventstore:db-client-java:4.0.0")
    implementation("io.ktor:ktor-server-core:2.2.1")
    implementation("io.ktor:ktor-server-netty:2.2.1")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("org.litote.kmongo:kmongo:4.8.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.apurebase:kgraphql:0.18.1")
    implementation("com.apurebase:kgraphql-ktor:0.18.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}