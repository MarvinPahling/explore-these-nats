plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "de.ostfalia"
version = "0.0.1-SNAPSHOT"
description = "backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("io.nats:jnats:2.20.4")

    // Logging dependencies (SLF4J is included with Spring Boot)
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
