import de.roamingthings.gradle.envConfig
import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
    `java-library`
    `maven-publish`
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

    compile("org.slf4j:slf4j-api:$slf4jVersion")

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
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

    val javadocJar by creating(Jar::class) {
        dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
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
            artifact(tasks["javadocJar"])
            artifactId = "semanticlogger"

            pom {
                name.set("Semantic Logging (Java Version)")
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

tasks.withType<Javadoc>() {
    if (JavaVersion.current().isJava8Compatible) {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
