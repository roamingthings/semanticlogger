plugins {
    id("net.researchgate.release") version "2.8.1"
}

tasks.register("build") {
    dependsOn(":semanticloggerJava:build", ":semanticloggerKotlin:build")
}
