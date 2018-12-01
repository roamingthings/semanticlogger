import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Year
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
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
    kotlin("jvm") version "1.3.10"
    `java-library`
    id("com.github.hierynomus.license") version "0.14.0"
    id("com.bmuschko.nexus") version "2.3.1"
}

if (JavaVersion.current().isJava9Compatible) {
    tasks.withType<GroovyCompile>().configureEach { options.compilerArgs.addAll(arrayOf("--release", "8")) }
}

if (JavaVersion.current().isJava8Compatible) {
    tasks.withType<GroovyCompile>().configureEach { options.compilerArgs.addAll(arrayOf("Xdoclint:none", "-quiet")) }
}

gradle.taskGraph.whenReady {
    if (hasTask(":publishPlugins")) {
        check("git diff --quiet --exit-code".execute(null, rootDir).waitFor() == 0) { "Working tree is dirty" }
        val process = "git describe --exact-match".execute(null, rootDir)
        check(process.waitFor() == 0) { "Version is not tagged" }
        version = process.text.trim().removePrefix("v")
    }
}

group = "de.roamingthings"

repositories {
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

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(arrayOf("-Xlint:all", "-Werror"))
    }

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

    named<Task>("check") {
        dependsOn(verifyKtlint)
    }

    register("ktlint", JavaExec::class) {
        description = "Fix Kotlin code style violations."
        classpath = ktlint
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "**/*.gradle.kts", "**/*.kt")
    }
}

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

fun String.execute(envp: Array<String>?, workingDir: File?) =
    Runtime.getRuntime().exec(this, envp, workingDir)

val Process.text: String
    get() = inputStream.bufferedReader().readText()

apply(from = "gradle/circleci.gradle.kts")
apply(from = "gradle/publish.gradle")
