package com.halcyonmobile.plugin.publish.custom.github

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

open class GeneratePublishScriptForGitHubActionsTask : DefaultTask() {

    @TaskAction
    fun howToPublish() {
        val stream = this::class.java.classLoader.getResourceAsStream("github_publish_actions_template.yml")
        val bufferedReader = BufferedReader(InputStreamReader(stream))

        val outputFile = prepareFiles()

        val outputFileWriter = outputFile.bufferedWriter()
        bufferedReader.lines().map(::replaceTemplateVariables).forEach {
            outputFileWriter.append(it)
            outputFileWriter.newLine()
        }

        bufferedReader.close()
        outputFileWriter.close()
    }

    private fun prepareFiles(): File {
        File(".github").takeUnless { it.exists() }?.mkdir()
        File(".github/workflows").takeUnless { it.exists() }?.mkdir()
        val outputFile = File(".github/workflows/release-package.yml")
        outputFile.delete()
        outputFile.createNewFile()

        return outputFile
    }

    private fun replaceTemplateVariables(line: String): String =
        line.replace(USERNAME_TO_REPLACE, GITHUB_USERNAME_KEY)
            .replace(PASSWORD_TO_REPLACE, GITHUB_PASSWORD_KEY)

    companion object {
        private const val USERNAME_TO_REPLACE = "{username_to_replace}"
        private const val PASSWORD_TO_REPLACE = "{password_to_replace}"
    }
}