package com.halcyonmobile.plugin.publish.artifactory

import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar

class JavaLibraryPublishToArtifactory : BasePublishToArtifactory() {

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
        sourcesJar.classifier = "sources"
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
