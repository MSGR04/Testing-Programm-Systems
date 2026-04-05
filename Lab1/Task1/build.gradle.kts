import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    id("java")
    id("jacoco")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/Main.class",
                    "**/*Main.class",
                    "**/*Application.class"
                )
            }
        })
    )

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}