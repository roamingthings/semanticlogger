import de.roamingthings.gradle.envConfig
import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    `java-library`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.dokka") version "0.10.0"
    id("net.researchgate.release") version "2.8.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
    id("de.roamingthings.jvmtest")
    id("de.roamingthings.librarylicense")
    id("de.roamingthings.librarysigning")
}

group = "de.roamingthings"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val lombokVersion: String by project
    val slf4jVersion: String by project

    compile(kotlin("stdlib-jdk8"))
    compile("org.slf4j:slf4j-api:$slf4jVersion")

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

/***
 * Linting
 */
ktlint {
    version.set("0.35.0")
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.JSON)
    }
    filter {
        exclude("**/style-violations.kt")
    }
}

tasks.named("compileKotlin") {
    dependsOn("ktlintCheck")
}

/***
 * Documentation generation
 */
tasks {
    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
        configuration {
            moduleName = rootProject.name
        }
    }
}

/***
 * Java Configuration and Tasks
 */
java {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    if (JavaVersion.current().isJava9Compatible) {
        options.compilerArgs.addAll(arrayOf("--release", "8"))
    }
    options.compilerArgs.addAll(arrayOf("-Xlint:all", "-Werror"))
}

/**
 * Distribution
 */
tasks {
    val sourcesJar by creating(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    val dokkaJar by creating(Jar::class) {
        dependsOn(dokka)
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "A Library to add semantic logging to your JVM project using SLF4J."
        archiveClassifier.set("javadoc")
        from(dokka)
    }

    artifacts {
        archives(sourcesJar)
        archives(dokkaJar)
        archives(jar)
    }
}

/**
 * Publishing
 */
val repositoryUser by envConfig()
val repositoryPassword by envConfig()
publishing {
    repositories {
        maven {
            name = "MavenCentral"
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = repositoryUser
                password = repositoryPassword
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["dokkaJar"])
            artifactId = "semanticlogger-kt"

            pom {
                name.set("Semantic Logging (Kotlin Version)")
                description.set("A Library to add semantic logging to your JVM project using SLF4J.")
                url.set("https://github.com/roamingthings/semanticlogger")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("roamingthings")
                        name.set("Alexander Sparkowsky")
                        email.set("info@roamingthings.de")
                    }
                }
                scm {
                    connection.set("https://github.com/roamingthings/semanticlogger.git")
                    developerConnection.set("https://github.com/roamingthings/semanticlogger.git")
                    url.set("https://github.com/roamingthings/semanticlogger")
                }
            }
        }
    }
}

release {
    buildTasks = listOf("build")
}

tasks.withType<Javadoc>() {
    if (JavaVersion.current().isJava8Compatible) {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
