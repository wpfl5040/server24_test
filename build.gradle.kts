import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.hyunjinmoon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    implementation("com.corundumstudio.socketio:netty-socketio:1.7.19")
//    implementation("io.scalecube:socketio:2.3.4")
    implementation("org.slf4j:slf4j-api:1.7.5")
    implementation("org.slf4j:slf4j-log4j12:1.7.5")
    implementation ("org.apache.logging.log4j:log4j-api-kotlin:1.1.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}