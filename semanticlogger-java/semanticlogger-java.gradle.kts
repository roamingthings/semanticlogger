import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import java.time.Year
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

plugins {
    `java-library`
    `maven-publish`
    signing
    id("com.github.hierynomus.license") version "0.15.0"
    id("net.researchgate.release") version "2.8.1"
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
