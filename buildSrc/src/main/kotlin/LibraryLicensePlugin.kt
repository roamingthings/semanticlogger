package de.roamingthings.gradle

import nl.javadude.gradle.plugins.license.LicenseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import java.time.Year

open class LibraryLicensePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.configureLicensePlugin()
        project.configureLicense()
    }
}

internal fun Project.configureLicensePlugin() {
    plugins.apply("com.github.hierynomus.license")
}

internal fun Project.configureLicense() =
    this.extensions.getByType<LicenseExtension>().run {
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
