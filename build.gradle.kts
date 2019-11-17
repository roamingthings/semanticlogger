import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.time.Year
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm") version "1.3.50"
    id("com.github.hierynomus.license") version "0.15.0"
    id("org.jetbrains.dokka") version "0.10.0"
    id("net.researchgate.release") version "2.8.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
}

group = "de.roamingthings"

fun envConfig() = object : ReadOnlyProperty<Any?, String?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String? =
        if (ext.has(property.name)) {
            ext[property.name] as? String
        } else {
            System.getenv(property.name)
        }
}

val repositoryUser by envConfig()
val repositoryPassword by envConfig()
val signingKeyId by envConfig()
val signingSecretKey by envConfig()
val signingPassword by envConfig()
val signingSecretKeyRingFile by envConfig()

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val assertjVersion: String by project
    val junitJupiterVersion: String by project
    val lombokVersion: String by project
    val mockitoVersion: String by project
    val slf4jVersion: String by project

    compile(kotlin("stdlib-jdk8"))
    compile("org.slf4j:slf4j-api:$slf4jVersion")

    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    compileOnly("org.projectlombok:lombok:$lombokVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")

    testCompile("org.assertj:assertj-core:$assertjVersion")
    testCompile("org.mockito:mockito-core:$mockitoVersion")
}

tasks.withType<Test>().configureEach {
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
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokka)
    dependsOn(tasks.dokka)
}

/**
 * Publishing
 */
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
                    connection.set("https://github.com/roamingthings/semanticlogger.git")
                    developerConnection.set("https://github.com/roamingthings/semanticlogger.git")
                    url.set("https://github.com/roamingthings/semanticlogger")
                }
            }
        }
    }
}

signing {
    if (!signingKeyId.isNullOrEmpty()) {
        project.ext["signing.keyId"] = signingKeyId
        project.ext["signing.password"] = signingPassword
        project.ext["signing.secretKeyRingFile"] = signingSecretKeyRingFile

        logger.info("Signing key id provided. Sign artifacts for $project.")

        isRequired = true
    } else if (!signingSecretKey.isNullOrEmpty()) {
        useInMemoryPgpKeys(signingSecretKey, signingPassword)
    } else {
        logger.warn("${project.name}: Signing key not provided. Disable signing for  $project.")
        isRequired = false
    }

    sign(publishing.publications)
}

release {
    buildTasks = listOf("build", "publish")
}

tasks {
    named<Javadoc>("javadoc") {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }
}
