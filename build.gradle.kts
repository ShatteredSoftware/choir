import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

repositories {
    mavenCentral()
}

group = "software.shattered"
val version: String by project


tasks.processResources {
    expand(
        "version" to version,
    )
}

tasks.test {
    useJUnitPlatform()
}

group = "software.shattered"

repositories {
    mavenLocal()
    maven( "https://repo.papermc.io/repository/maven-public/")
    maven (url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven (url = "https://repo.codemc.io/repository/maven-public/")
    mavenCentral()
    maven (url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven (url = "https://oss.sonatype.org/content/repositories/central")
    maven (url = "https://repo.codemc.io/repository/nms/")
    maven (url = "https://jitpack.io")
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.19.4-R0.1-SNAPSHOT")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.ShatteredSoftware:mini18n:1.0.0")

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
}

tasks.processResources {
    expand(
        "version" to version,
    )
}

tasks.test {
    useJUnitPlatform()
}
