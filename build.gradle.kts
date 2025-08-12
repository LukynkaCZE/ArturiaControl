plugins {
    kotlin("jvm") version "2.2.0"
}

group = "cz.lukynka.arturia.control"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://mvn.devos.one/releases")
    maven("https://mvn.devos.one/snapshots")
}

dependencies {
    testImplementation(kotlin("test"))
    api("io.ktor:ktor-server-netty:3.1.2")
    implementation("org.slf4j:slf4j-nop:2.0.9")
    api("cz.lukynka:pretty-log:1.5")
    api("it.unimi.dsi:fastutil:8.5.13")
    api("io.github.dockyardmc:tide:1.7")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}