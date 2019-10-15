package com.halcyonmobile.plugin.publish.artifactory

import com.android.build.gradle.LibraryExtension
import digital.wup.android_maven_publish.AndroidMavenPublishPlugin
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar

class AarLibraryPublishToArtifactory : BasePublishToArtifactory() {

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
        sourcesJar.classifier = "sources"
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