package com.halcyonmobile.plugin.publish.custom.github

import org.gradle.api.Project

const val GITHUB_PACKAGE_URL = "githubPackagePath"
const val GITHUB_USERNAME_KEY = "GITHUB_USERNAME"
const val GITHUB_PASSWORD_KEY = "GITHUB_TOKEN"

val hasGitHubAccess: Boolean get() = System.getenv(GITHUB_USERNAME_KEY) != null

val githubUserName: String
    get() = System.getenv(GITHUB_USERNAME_KEY) ?: throw IllegalStateException()

val githubPassword: String
    get() = System.getenv(GITHUB_PASSWORD_KEY) ?: throw IllegalStateException()

val Project.githubPackageUrl: String
    get() : String {
        if (!hasProperty(GITHUB_PACKAGE_URL)) {
            throw IllegalStateException("$GITHUB_PACKAGE_URL is not defined. Make sure to define it in your root build.gradle. This is the url for the source code, most probably github.")
        }
        return "https://maven.pkg.github.com/${property(GITHUB_PACKAGE_URL) as String}"
    }
