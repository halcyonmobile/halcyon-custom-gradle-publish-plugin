package com.halcyonmobile.plugin.publish.custom.artifactory

import org.gradle.api.Project

const val ARTIFACTORY_USERNAME_KEY = "artifactory_username"
const val ARTIFACTORY_PASSWORD_KEY = "artifactory_password"
private const val ARTIFACTORY_REPO_KEY = "artifactoryRepoKey"
private const val ARTIFACTORY_DEFAULT_REPO_KEY = "libs-release-local"
const val ARTIFACTORY_URL = "https://artifactory.build.halcyonmobile.com/artifactory"

val hasArtifactoryAccess: Boolean get() = System.getenv(ARTIFACTORY_USERNAME_KEY) != null

val artifactoryUserName: String
    get() = System.getenv(ARTIFACTORY_USERNAME_KEY) ?: throw IllegalStateException("artifactory username not found")

val artifactoryPassword: String
    get() = System.getenv(ARTIFACTORY_PASSWORD_KEY) ?: throw IllegalStateException("artifactory password not found")

val Project.repoKey: String
    get() = if (hasProperty(ARTIFACTORY_REPO_KEY)) property(ARTIFACTORY_REPO_KEY) as String else ARTIFACTORY_DEFAULT_REPO_KEY

