package de.roamingthings.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.withType

open class JvmTestPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (project.plugins.hasPlugin(JavaPlugin::class.java)) {
            project.configureTestDependencies()
            project.configureTest()
        }
    }
}

internal fun Project.configureTestDependencies() {
    val mockito = "org.mockito:mockito-core:${extra["mockitoVersion"]}"
    val assertj = "org.assertj:assertj-core:${extra["assertjVersion"]}"
    val jupiterApi = "org.junit.jupiter:junit-jupiter-api:${extra["junitJupiterVersion"]}"
    val jupiterEngine = "org.junit.jupiter:junit-jupiter-engine:${extra["junitJupiterVersion"]}"

    dependencies {
        add("testCompile", mockito)
        add("testCompile", assertj)
        add("testImplementation", jupiterApi)
        add("testRuntimeOnly", jupiterEngine)
    }
}

internal fun Project.configureTest() = this.tasks.withType<Test>().configureEach {
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
