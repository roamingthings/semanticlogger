import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Year
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.serialization.js.DynamicTypeDeserializer.id

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlinVersion"]}")
    }
}

plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm") version "1.3.10"
    id("com.github.hierynomus.license") version "0.14.0"
    id("org.jetbrains.dokka") version "0.9.17"
}

group = "de.roamingthings"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.slf4j:slf4j-api:1.7.25")

    annotationProcessor("org.projectlombok:lombok:${extra["lombokVersion"]}")
    compileOnly("org.projectlombok:lombok:${extra["lombokVersion"]}")

    testImplementation("junit:junit:${extra["junit4Version"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${extra["junitJupiterVersion"]}")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${extra["junitJupiterVersion"]}")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:${extra["junitJupiterVersion"]}")

    testCompile("org.junit.platform:junit-platform-launcher:${extra["junitJupiterPlatformVersion"]}")
    testCompile("org.junit.platform:junit-platform-runner:${extra["junitJupiterPlatformVersion"]}")
    testCompile("org.assertj:assertj-core:${extra["assertjVersion"]}")
    testCompile("org.mockito:mockito-all:${extra["mockitoVersion"]}")
}

/***
 * Test Configuration and Tasks
 */
tasks {
    named<Test>("test") {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            showExceptions = true
            showStackTraces = true
            exceptionFormat = FULL
            showCauses = true
            showStackTraces = true
        }
    }
}

/**
 * Kotlin Configuration and Tasks
 */
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val ktlint by configurations.creating

dependencies {
    ktlint("com.github.shyiko:ktlint:0.29.0")
}

tasks {
    val verifyKtlint by registering(JavaExec::class) {
        group = "verification"
        description = "Check Kotlin code style."
        classpath = ktlint
        main = "com.github.shyiko.ktlint.Main"
        args("**/*.gradle.kts", "**/*.kt")
    }

    named<Task>("compileKotlin") {
        dependsOn(verifyKtlint)
    }

    register("ktlint", JavaExec::class) {
        description = "Fix Kotlin code style violations."
        classpath = ktlint
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "**/*.gradle.kts", "**/*.kt")
    }

    register("dokkaJavadoc", DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/dokkaJavadoc"
    }
/*
    val dokka by registering(org.jetbrains.dokka.gradle.DokkaTask::class) {
    dependsOn jar
            group = "documentation"
    description = "Generates Kotlin API documentation."
    moduleName = "reactor-core"
    jdkVersion = 8

    outputFormat = "html"
    outputDirectory = new File(project.buildDir, "docs/kdoc")

    //this is needed so that links to java classes are resolved
    doFirst {
        classpath += project.jar.outputs.files.getFiles()
        classpath += project.sourceSets.main.compileClasspath
    }
    //this is needed so that the kdoc only generates for kotlin classes
    //(default kotlinTasks sourceSet also includes java)
    kotlinTasks {
    }
    processConfigurations = []
    sourceDirs = files("src/main/kotlin")
*/
}

/***
 * Java Configuration and Tasks
 */
if (JavaVersion.current().isJava8Compatible) {
    tasks.withType<GroovyCompile>().configureEach { options.compilerArgs.addAll(arrayOf("Xdoclint:none", "-quiet")) }
}

if (JavaVersion.current().isJava9Compatible) {
    tasks.withType<GroovyCompile>().configureEach { options.compilerArgs.addAll(arrayOf("--release", "8")) }
}

java {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
}

tasks {
    withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(arrayOf("-Xlint:all", "-Werror"))
    }
}

/***
 * License Handling
 */
license {
    header = rootProject.file("doc/APL.header")
    skipExistingHeaders = true
    mapping("java", "SLASHSTAR_STYLE")
    mapping("kt", "SLASHSTAR_STYLE")
    include("**/*.java")
    include("**/*.kt")
    exclude("**/default*.*")
    exclude("**/*Test*.kt")
    exclude("**/*IT.kt")

    (this as ExtensionAware).extra["year"] = Year.now()
    (this as ExtensionAware).extra["name"] = "Alexander Sparkowsky"
}

/**
 * Distribution
 */
task<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    classifier = "sources"
}

task<Jar>("javadocJar") {
    from(tasks["dokkaJavadoc"])
    classifier = "javadoc"
}

/**
 * Publishing
 */
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "semanticlogger"
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                name.set("Semantic Logging")
                description.set("A Library to add semantic logging to your JVM project using SLF4J")
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
                    connection.set("scm:git@github.com:roamingthings/semanticlogger.git")
                    developerConnection.set("scm:git@github.com:roamingthings/semanticlogger.git")
                    url.set("https://github.com/roamingthings/semanticlogger")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            authentication {
                credentials {
                    username =
                            (if (project.hasProperty("nexusUsername"))
                                project.properties["nexusUsername"]
                            else
                                System.getenv("nexusUsername")) as String
                    password =
                            (if (project.hasProperty("nexusPassword"))
                                project.properties["nexusPassword"]
                            else
                                System.getenv("nexusPassword")) as String
                }
            }
        }
    }
}

tasks.withType<Sign> {
    onlyIf { project.hasProperty("signing.keyId") }
}

signing {
    useGpgCmd()
    isRequired = false
    sign(publishing.publications["mavenJava"])
}

tasks {
    named<Javadoc>("javadoc") {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }
}

fun String.execute(envp: Array<String>?, workingDir: File?) =
    Runtime.getRuntime().exec(this, envp, workingDir)

val Process.text: String
    get() = inputStream.bufferedReader().readText()

apply(from = "gradle/circleci.gradle.kts")
