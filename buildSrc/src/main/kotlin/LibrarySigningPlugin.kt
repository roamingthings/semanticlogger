package de.roamingthings.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.plugins.signing.SigningExtension
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Project.envConfig() = object : ReadOnlyProperty<Any?, String?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String? =
        if (extensions.extraProperties.has(property.name)) {
            extensions.extraProperties[property.name] as? String
        } else {
            System.getenv(property.name)
        }
}

open class LibrarySigningPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.configureSigningPlugin()
        project.configureSigning()
    }
}

internal fun Project.configureSigningPlugin() {
    plugins.apply("org.gradle.signing")
}

internal fun Project.configureSigning() = this.extensions.getByType<SigningExtension>().run {
    val signingKeyId by envConfig()
    val signingSecretKey by envConfig()
    val signingPassword by envConfig()
    val signingSecretKeyRingFile by envConfig()

    if (!signingKeyId.isNullOrEmpty()) {
        extensions.extraProperties["signing.keyId"] = signingKeyId
        extensions.extraProperties["signing.password"] = signingPassword
        extensions.extraProperties["signing.secretKeyRingFile"] = signingSecretKeyRingFile

        logger.info("Signing key id provided. Sign artifacts for $project.")

        isRequired = true
    } else if (!signingSecretKey.isNullOrEmpty()) {
        useInMemoryPgpKeys(signingSecretKey, signingPassword)
    } else {
        logger.warn("${project.name}: Signing key not provided. Disable signing for  $project.")
        isRequired = false
    }

    val publishing: PublishingExtension = extensions.get("publishing") as PublishingExtension
    sign(publishing.publications)
}
