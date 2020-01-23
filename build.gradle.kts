repositories {
    mavenCentral()
}

group = "com.github.pawelkow"
version = "1.0"

plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val jUnitVersion by extra ("5.5.2")

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jUnitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${jUnitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${jUnitVersion}")
}

tasks {
    test {
        useJUnitPlatform()
    }
}