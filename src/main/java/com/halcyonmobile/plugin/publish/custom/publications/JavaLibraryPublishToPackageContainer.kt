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

import com.halcyonmobile.plugin.publish.custom.libraryArtifactId
import com.halcyonmobile.plugin.publish.custom.libraryGroupId
import com.halcyonmobile.plugin.publish.custom.libraryVersion
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar

class JavaLibraryPublishToPackageContainer : BasePublishToPackageContainer() {

    override fun applyPlugins(project: Project) {
        project.plugins.apply(MavenPublishPlugin::class.java)
    }

    /**
     * Equivalent to
     *
     * ```
     * task sourcesJar(type: Jar, dependsOn: classes) {
     *    classifier = 'sources'
     *    from sourceSets.main.allSource
     * }
     * ```
     */
    override fun Project.createSourcesJarTask(): Task {
        val sourcesJar = tasks.create("sourcesJar", Jar::class.java)
        sourcesJar.archiveClassifier.set("sources")
        sourcesJar.from(convention.getPlugin(JavaPluginConvention::class.java).sourceSets.asMap["main"]!!.allSource)

        return sourcesJar.dependsOn("classes")
    }

    /**
     * Equivalent to
     *
     * ```
     * publishing {
     *     publications {
     *         mavenJar(MavenPublication) {
     *             from components.java
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
        val mavenPublication = (publishingExtension.publications as PolymorphicDomainObjectContainer<Publication>).create("mavenJar", MavenPublication::class.java)
        mavenPublication.groupId = project.libraryGroupId
        mavenPublication.artifactId = project.libraryArtifactId
        mavenPublication.version = project.libraryVersion
        mavenPublication.artifact(sourcesJarTask)
        mavenPublication.from(project.components.findByName("java"))

        return mavenPublication
    }
}
