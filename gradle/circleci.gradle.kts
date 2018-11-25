import org.gradle.util.GradleVersion

tasks.register("generateCircleciConfig", GenerateCircleciConfig::class) {
    jdks = listOf(8, 11)
    outputFile = file(".circleci/config.yml")
}

open class GenerateCircleciConfig : DefaultTask() {
    @get:Input
    lateinit var jdks: List<Int>

    @get:OutputFile
    lateinit var outputFile: File

    @TaskAction
    fun generate() {
        // XXX: this currently assumes that CrossVersion#jdks are a subset of jdks

        outputFile.writeText(
            (
                    """
            #
            # This is a generated file
            #
            platforms:
              - &defaults
                environment:
                  - GRADLE_OPTS: -Dorg.gradle.daemon=false
                  - JAVA_TOOL_OPTIONS: -XX:MaxRAM=4g -XX:ParallelGCThreads=2
            """ + jdks.map {
                        """
              - &java$it
                <<: *defaults
                docker:
                  - image: circleci/openjdk:$it-jdk
                """
                    }.joinToString(separator = "") + """
            caches:
              workspace:
                - &persist-workspace
                  persist_to_workspace:
                    root: .
                    paths: .
                - &attach-workspace
                  attach_workspace:
                    at: .
              test_results:
                - &store-test-results
                  store_test_results:
                    paths: build/test-results/
              dependencies:
            """ + jdks.map {
                        """
                - &save-gradle-dependencies-java$it
                  save_cache:
                    name: Saving Gradle dependencies
                    key: v3-gradle-java$it-{{ checksum "build.gradle.kts" }}
                    paths:
                      - ~/.gradle/caches/modules-2/
                - &restore-gradle-dependencies-java$it
                  restore_cache:
                    name: Restoring Gradle dependencies
                    keys:
                      - v3-gradle-java$it-{{ checksum "build.gradle.kts" }}
                """
                    }.joinToString(separator = "") + """
              wrapper:
            """ + (mapOf("current" to GradleVersion.current().getVersion())).map { (name, version) ->
                        """
                - &save-gradle-wrapper-$name
                  save_cache:
                    name: Saving Gradle wrapper $version
                    key: v2-gradle-wrapper-$version
                    paths:
                      - ~/.gradle/wrapper/dists/gradle-$version-bin/
                      - ~/.gradle/caches/$version/generated-gradle-jars/
                      - ~/.gradle/notifications/$version/
                - &restore-gradle-wrapper-$name
                  restore_cache:
                    name: Restoring Gradle wrapper $version
                    keys:
                      - v2-gradle-wrapper-$version
                """
                    }.joinToString(separator = "") + """
            version: 2
            jobs:
              checkout_code:
                <<: *java${jdks.first()}
                steps:
                  - checkout
                  - run:
                      name: Remove Git tracking files (reduces workspace size)
                      command: rm -rf .git/
                  - *persist-workspace
            """ + jdks.map {
                        """
              java$it:
                <<: *java$it
                steps:
                  - *attach-workspace
                  - *restore-gradle-dependencies-java$it
                  - *restore-gradle-wrapper-current
                  - run:
                      name: Build
                      command: ./gradlew build
                  - *store-test-results
                  - *save-gradle-wrapper-current
                  - *save-gradle-dependencies-java$it
                  - *persist-workspace
                """
                    }.joinToString(separator = "") + """
            workflows:
              version: 2
              tests:
                jobs:
                  - checkout_code
            """ + jdks.mapIndexed { index, it ->
                        """
                  - java$it:
                      requires:
                        - ${if (index > 0) "java${jdks.first()}" else "checkout_code"}
                """
                    }.joinToString(separator = "")
                    ).trimIndent()
        )
    }
}
