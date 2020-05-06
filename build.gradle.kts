repositories {
    mavenCentral()
}

group = "com.github.pawelkow"
version = "1.0"

plugins {
    `java-library`
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("CheckedFunctions") {
            from(components.getByName("java"))
            pom {
                name.set("Checked Functions")
                description.set("A handy Java module that extends capabilities of functions added in JDK 8 by including checked exceptions.")
                url.set("https://github.com/pawelkowalski92/checked-functions")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("pawelkowalski92")
                        name.set("Paweł Kowalski")
                        email.set("34212272+pawelkowalski92@users.noreply.github.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/pawelkowalski92/checked-functions.git")
                    url.set("https://github.com/pawelkowalski92/checked-functions")
                }
            }
        }
    }
}

val jUnitVersion by extra("5.5.2")

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