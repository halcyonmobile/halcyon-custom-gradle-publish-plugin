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

import com.halcyonmobile.plugin.publish.custom.howtotask.HowToIntegrateGitHubActionsTask
import com.halcyonmobile.plugin.publish.custom.howtotask.HowToPublishTask
import com.halcyonmobile.plugin.publish.custom.artifactory.ARTIFACTORY_URL
import com.halcyonmobile.plugin.publish.custom.artifactory.PublishToArtifactoryTask
import com.halcyonmobile.plugin.publish.custom.artifactory.artifactoryPassword
import com.halcyonmobile.plugin.publish.custom.artifactory.artifactoryUserName
import com.halcyonmobile.plugin.publish.custom.artifactory.hasArtifactoryAccess
import com.halcyonmobile.plugin.publish.custom.artifactory.repoKey
import com.halcyonmobile.plugin.publish.custom.bintray.PublishToBintrayTask
import com.halcyonmobile.plugin.publish.custom.bintray.bintrayLicense
import com.halcyonmobile.plugin.publish.custom.bintray.bintrayPassword
import com.halcyonmobile.plugin.publish.custom.bintray.bintrayRepo
import com.halcyonmobile.plugin.publish.custom.bintray.bintrayUserName
import com.halcyonmobile.plugin.publish.custom.bintray.bintrayVcsUrl
import com.halcyonmobile.plugin.publish.custom.bintray.hasBintrayAccess
import com.halcyonmobile.plugin.publish.custom.github.GeneratePublishScriptForGitHubActionsTask
import com.halcyonmobile.plugin.publish.custom.github.PublishToGithubTask
import com.halcyonmobile.plugin.publish.custom.github.githubPackageUrl
import com.halcyonmobile.plugin.publish.custom.github.githubPassword
import com.halcyonmobile.plugin.publish.custom.github.githubUserName
import com.halcyonmobile.plugin.publish.custom.github.hasGitHubAccess
import com.halcyonmobile.plugin.publish.custom.libraryArtifactId
import com.halcyonmobile.plugin.publish.custom.libraryVersion
import com.halcyonmobile.plugin.publish.custom.nakedLibraryGroupId
import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublishingExtension
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPlugin
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.DoubleDelegateWrapper
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask

abstract class BasePublishToPackageContainer : Plugin<Project> {

    override fun apply(project: Project) {
        if (hasArtifactoryAccess) {
            project.plugins.apply(ArtifactoryPlugin::class.java)
        }
        if (hasBintrayAccess) {
            project.plugins.apply(BintrayPlugin::class.java)
        }

        applyPlugins(project)

        project.rootProject.createHowToTask()
        project.rootProject.createHowToIntegrateGitHubActionsTask()
        project.rootProject.createGeneratePublishScriptForGitHubActionsTask()
        project.rootProject.createPublishToArtifactoryTask()
        project.rootProject.createPublishToBintrayTask()
        project.rootProject.createPublishToGithubTask()
        val sourcesJarTask = project.createSourcesJarTask()
        val mavenPublication = createMavenPublication(project, sourcesJarTask)
        if (hasArtifactoryAccess) {
            configureArtifactory(project, mavenPublication)
        }
        if (hasBintrayAccess) {
            configureBintray(project, mavenPublication)
        }
        if (hasGitHubAccess) {
            configureGitHub(project)
        }
    }

    protected open fun applyPlugins(project: Project) {
    }

    protected abstract fun Project.createSourcesJarTask(): Task

    private fun Project.createHowToTask() {
        tasks.maybeCreate("howToPublish", HowToPublishTask::class.java)
    }

    private fun Project.createHowToIntegrateGitHubActionsTask() {
        tasks.maybeCreate("howToIntegrateGitHubActions", HowToIntegrateGitHubActionsTask::class.java)
    }

    private fun Project.createGeneratePublishScriptForGitHubActionsTask() {
        tasks.maybeCreate("generatePublishScriptForGitHubActions", GeneratePublishScriptForGitHubActionsTask::class.java)
    }

    private fun Project.createPublishToArtifactoryTask() {
        tasks.maybeCreate("publishToArtifactory", PublishToArtifactoryTask::class.java)
    }

    private fun Project.createPublishToBintrayTask() {
        tasks.maybeCreate("publishToBintray", PublishToBintrayTask::class.java)
    }

    private fun Project.createPublishToGithubTask() {
        tasks.maybeCreate("publishToGitHub", PublishToGithubTask::class.java)
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
                    publisherConfig.propertyMissing("username", artifactoryUserName)
                    publisherConfig.propertyMissing("password", artifactoryPassword)
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
     * ```
     */
    private fun configureBintray(project: Project, publication: Publication) {
        val bintrayExtension = project.extensions.findByType(BintrayExtension::class.java) as BintrayExtension
        bintrayExtension.user = bintrayUserName
        bintrayExtension.key = bintrayPassword
        bintrayExtension.setPublications(publication.name)
        bintrayExtension.publish = true
        bintrayExtension.override = true
        bintrayExtension.pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
            repo = project.bintrayRepo
            name = "${project.nakedLibraryGroupId}:${project.libraryArtifactId}"
            userOrg = "halcyonmobiledevteam"
            vcsUrl = project.bintrayVcsUrl
            setLicenses(project.bintrayLicense)
            version(delegateClosureOf<BintrayExtension.VersionConfig> {
                name = project.libraryVersion
            })
        })
    }

    /**
     * ```
     * publishing {
     *      repositories {
     *           maven {
     *                name = "GitHubPackages"
     *                url = uri("https://maven.pkg.github.com/${System.env("github_package_path"}")
     *                credentials {
     *                     username = System.getenv("GITHUB_USERNAME")
     *                     password = System.getenv("GITHUB_TOKEN")
     *                }
     *           }
     *      }
     * }
     * ```
     */
    private fun configureGitHub(project: Project) {
        val publishingExtension = project.extensions.findByName("publishing") as PublishingExtension
        publishingExtension.repositories { repositories ->
            repositories.maven(delegateClosureOf<MavenArtifactRepository> {
                name = "GitHubPackages"
                url = project.uri(project.githubPackageUrl)
                credentials { credentials ->
                    credentials.username = githubUserName
                    credentials.password = githubPassword
                }
            })
        }
    }

    /**
     * Creates a Groovy closure.
     */
    private fun <T> Any.delegateClosureOf(action: T.() -> Unit) =
        object : Closure<Unit>(this, this) {
            @Suppress("unused", "UNCHECKED_CAST") // to be called dynamically by Groovy
            fun doCall() = (delegate as T).action()
        }

    private fun <T> delegateActionOf(action: T.() -> Unit) = Action<T> { arg -> action.invoke(arg as T) }
}