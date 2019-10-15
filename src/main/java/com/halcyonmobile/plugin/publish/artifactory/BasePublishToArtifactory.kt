package com.halcyonmobile.plugin.publish.artifactory

import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import groovy.lang.Closure
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.Publication
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPlugin
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.DoubleDelegateWrapper
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask

abstract class BasePublishToArtifactory : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(ArtifactoryPlugin::class.java)
        if (!project.hasBintrayAccess) {
            System.err.println("If you wish to publish bintray, set up your access, similar to artifactory: set the username and password in your gradle.properties with $BINTRAY_USERNAME_KEY & $BINTRAY_PASSWORD_KEY keys.\nTo get these login to halcyon github, then to bintray with that github and in settings you can find the needed username and password. github access is listen on the wiki.")
        } else {
            project.plugins.apply(BintrayPlugin::class.java)
        }
        applyPlugins(project)

        project.rootProject.createHowToTask()
        project.rootProject.createPublishToArtifactoryTask()
        project.rootProject.createPublishToBintrayTask()
        val sourcesJarTask = project.createSourcesJarTask()
        val mavenPublication = createMavenPublication(project, sourcesJarTask)
        configureArtifactory(project, mavenPublication)
        if (project.hasBintrayAccess) {
            configureBintray(project, mavenPublication)
        }
    }

    protected open fun applyPlugins(project: Project) {
    }

    protected abstract fun Project.createSourcesJarTask(): Task

    private fun Project.createHowToTask() {
        tasks.maybeCreate("howToPublish", HowToPublishTask::class.java)
    }

    private fun Project.createPublishToArtifactoryTask(){
        tasks.maybeCreate("publishToArtifactory", PublishToArtifactoryTask::class.java)
    }

    private fun Project.createPublishToBintrayTask(){
        tasks.maybeCreate("publishToBintray", PublishToBintrayTask::class.java)
    }

    protected abstract fun createMavenPublication(project: Project, sourcesJarTask: Task): Publication

    /**
     * ```
     * artifactory {
     *      contextUrl = 'https://artifactory.build.halcyonmobile.com/artifactory'
     *      publish {
     *           contextUrl = 'https://artifactory.build.halcyonmobile.com/artifactory'
     *           repository {
     *                repoKey = 'libs-release-local'
     *                username = artifactory_username
     *                password = artifactory_password
     *                maven = true
     *           }
     *
     *           defaults {
     *                publications('mavenJar')
     *                publishArtifacts = true
     *           }
     *      }
     * }
     * ```
     */
    private fun configureArtifactory(project: Project, publication: Publication) {
        val artifactoryPluginConvention = (project.convention.findPlugin(ArtifactoryPluginConvention::class.java) as ArtifactoryPluginConvention)
        artifactoryPluginConvention.artifactory(delegateClosureOf<ArtifactoryPluginConvention> {
            setContextUrl(ARTIFACTORY_URL)
            publish(delegateClosureOf<PublisherConfig> {
                setContextUrl(ARTIFACTORY_URL)
                repository(delegateClosureOf<DoubleDelegateWrapper> {
                    publisherConfig.propertyMissing("repoKey", project.repoKey)
                    publisherConfig.propertyMissing("username", project.artifactoryUserName)
                    publisherConfig.propertyMissing("password", project.artifactoryPassword)
                    publisherConfig.propertyMissing("maven", true)
                })
                defaults(delegateClosureOf<ArtifactoryTask> {
                    this.publications(publication)
                })
            })
        })
    }

    /**
     * ```
     * bintray {
     *      user = bintray_user
     *      key = bintray_key
     *      publications = ['mavenJar']
     *      publish = true
     *      override = true
     *      pkg {
     *           repo = 'maven'
     *           name = bintrayPackageName
     *           vcsUrl = 'https://github.com/halcyonmobiledev/ViewModelFactoryGenerator'
     *           licenses = ['Apache-2.0']
     *           version {
     *                name = libraryVersion
     *           }
     *      }
     * }
     */
    private fun configureBintray(project: Project, publication: Publication) {
        val bintrayExtension = project.extensions.findByType(BintrayExtension::class.java) as BintrayExtension
        bintrayExtension.user = project.bintrayUserName
        bintrayExtension.key = project.bintrayPassword
        bintrayExtension.setPublications(publication.name)
        bintrayExtension.publish = true
        bintrayExtension.override = true
        bintrayExtension.pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
            repo = project.bintrayRepo
            name = "${project.nakedLibraryGroupId}:${project.libraryArtifactId}"
            vcsUrl = project.bintrayVcsUrl
            setLicenses(project.bintrayLicense)
            version(delegateClosureOf<BintrayExtension.VersionConfig> {
                name = project.libraryVersion
            })
        })
    }

    /**
     * Creates a Groovy closure.
     */
    fun <T> Any.delegateClosureOf(action: T.() -> Unit) =
            object : Closure<Unit>(this, this) {
                @Suppress("unused", "UNCHECKED_CAST") // to be called dynamically by Groovy
                fun doCall() = (delegate as T).action()
            }
}