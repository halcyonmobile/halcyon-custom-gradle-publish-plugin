package com.halcyonmobile.plugin.publish.artifactory

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class HowToPublishTask : DefaultTask() {

    @TaskAction
    fun howToPublish() {
        println("How to publish:")
        println("Method one, run the following command")
        println("Artifactory: ./gradlew publishToArtifactory")
        println("Bintray:     ./gradlew publishToBintray")
        println("Method two, run the following command")
        println("Note: for step 3 you need both if both kind of libraries are published, one if only one type")
        println("0. open your terminal and run the following commands.")
        println("1.                ./gradlew clean")
        println("2.                ./gradlew assemble")
        println("3(java).          ./gradlew generatePomFileForMavenJarPublication")
        println("3(android).       ./gradlew generatePomFileForMavenAarPublication")
        println("4(artifactory).   ./gradlew artifactoryPublish")
        println("4(bintrayUpload). ./gradlew bintrayUpload")
    }
}
