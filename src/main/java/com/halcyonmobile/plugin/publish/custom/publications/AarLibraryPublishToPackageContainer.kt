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

package com.halcyonmobile.plugin.publish.custom.publications

import com.android.build.gradle.LibraryExtension
import com.halcyonmobile.plugin.publish.custom.libraryArtifactId
import com.halcyonmobile.plugin.publish.custom.libraryGroupId
import com.halcyonmobile.plugin.publish.custom.libraryVersion
import digital.wup.android_maven_publish.AndroidMavenPublishPlugin
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar

class AarLibraryPublishToPackageContainer : BasePublishToPackageContainer() {

    override fun applyPlugins(project: Project) {
        project.plugins.apply(AndroidMavenPublishPlugin::class.java)
    }

    /**
     * Equivalent to
     *
     * ```
     * task sourceJar(type: Jar) {
     *    from android.sourceSets.main.java.srcDirs
     *    classifier "sources"
     * }
     * ```
     */
    override fun Project.createSourcesJarTask(): Task {
        val sourcesJar = tasks.create("sourcesJar", Jar::class.java)
        sourcesJar.archiveClassifier.set("sources")
        sourcesJar.from(extensions.getByType(LibraryExtension::class.java).sourceSets.findByName("main")!!.java.srcDirs)

        return sourcesJar
    }

    /**
     * Equivalent to
     *
     * ```
     * publishing {
     *     publications {
     *         mavenAar(MavenPublication) {
     *             from components.android
     *
     *             groupId libraryGroupId
     *             version libraryVersion
     *             artifactId libraryArtifactId
     *
     *             artifact sourceJar
     *         }
     *     }
     * }
     * ```
     */
    override fun createMavenPublication(project: Project, sourcesJarTask: Task): Publication {
        val publishingExtension = project.extensions.findByName("publishing") as PublishingExtension
        val mavenPublication = (publishingExtension.publications as PolymorphicDomainObjectContainer<Publication>).create("mavenAar", MavenPublication::class.java)
        mavenPublication.groupId = project.libraryGroupId
        mavenPublication.artifactId = project.libraryArtifactId
        mavenPublication.version = project.libraryVersion
        mavenPublication.artifact(sourcesJarTask)
        mavenPublication.from(project.components.findByName("android"))

        return mavenPublication
    }

}