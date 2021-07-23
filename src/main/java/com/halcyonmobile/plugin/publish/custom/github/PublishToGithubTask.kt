package com.halcyonmobile.plugin.publish.custom.github

import com.halcyonmobile.plugin.publish.custom.ShellPublishTask
import java.io.InputStream

open class PublishToGithubTask : ShellPublishTask() {
    override val actualPublishTask: String get() = "publishMavenJarPublicationToGitHubPackagesRepository"
    private val actualAarPublishTask: String get() = "publishMavenAarPublicationToGitHubPackagesRepository"

    override fun verifyConfiguration(): Boolean {
        if (!hasGitHubAccess) {
            System.err.println("Project doesn't have bintray access, please provide the following configurations:")
            System.err.println("required configurations username: $GITHUB_USERNAME_KEY")
            System.err.println("required configurations token: $GITHUB_PASSWORD_KEY")
            System.err.println("configuration need to be set in the \$HOME/.bash_profile \"export KEY=value\" format")
            System.err.println("Additionally you need to provide the GitHub Package, in the local build.gradle")
            System.err.println("required configurations token: $GITHUB_PACKAGE_URL (is the format of halcyonmobile/halcyon-custom-gradle-publish-plugin)")
            return false
        }
        return true
    }

    override fun runActualTask(logger: (inputStream: InputStream, isError: Boolean) -> Unit) {
        runGradleTaskFromShell(actualPublishTask, logger)
            .runThisIfFailed { runGradleTaskFromShell(actualAarPublishTask, logger) }
            .showErrorAndThrowExceptionIfFailed()
    }
}