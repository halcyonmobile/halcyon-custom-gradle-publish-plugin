/*
 * Copyright (c) 2020 Halcyon Mobile.
 * https://www.halcyonmobile.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
