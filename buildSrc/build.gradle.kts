plugins{
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://plugins.gradle.org/m2/")
}

val kotlinVersion: String by project
val licensePluginVersion: String by project
dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
//    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    implementation("gradle.plugin.com.hierynomus.gradle.plugins:license-gradle-plugin:$licensePluginVersion")
}
