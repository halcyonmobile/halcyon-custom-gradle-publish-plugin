package com.halcyonmobile.plugin.publish.custom.howtotask

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class HowToIntegrateGitHubActionsTask : DefaultTask() {

    @TaskAction
    fun howToPublish() {
        println("How to integrate GitHub Actions:")
        println("1. Configure the project as for releasing to GitHub")
        println("2. ./gradlew generatePublishScriptForGitHubActions")
        println("3. verify the file is generated under project/.github/workflows/release-package.yml")
    }
}