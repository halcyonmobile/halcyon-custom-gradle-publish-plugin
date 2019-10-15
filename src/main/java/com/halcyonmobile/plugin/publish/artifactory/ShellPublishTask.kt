package com.halcyonmobile.plugin.publish.artifactory

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Defines a task which runs the following gradle commands in shell:
 * ./gradlew clean
 * ./gradlew assemble
 * ./gradlew generatePomFileForMavenJarPublication
 * ./gradlew generatePomFileForMavenAarPublication
 * ./gradlew [actualPublishTask]
 */
abstract class ShellPublishTask : DefaultTask() {

    abstract val actualPublishTask: String

    @TaskAction
    fun howToPublish() {
        val executor = Executors.newSingleThreadExecutor()
        val logger = executor.createLogger()
        runGradleTaskFromShell("clean", logger).showErrorAndThrowExceptionIfFailed()
        runGradleTaskFromShell("assemble", logger).showErrorAndThrowExceptionIfFailed()
        runGradleTaskFromShell("generatePomFileForMavenJarPublication", logger)
        runGradleTaskFromShell("generatePomFileForMavenAarPublication", logger)
        runGradleTaskFromShell(actualPublishTask, logger).showErrorAndThrowExceptionIfFailed()
    }

    private fun runGradleTaskFromShell(task: String, logger: (inputStream: InputStream, isError: Boolean) -> Unit): Int {
        println("running gradle task: ./gradlew $task")
        val process = Runtime.getRuntime().exec("./gradlew $task")
        logger(process.inputStream, false)
        logger(process.errorStream, true)
        return process.waitFor()
    }

    /**
     * Creates a logger which can be used in [runGradleTaskFromShell]
     */
    private fun ExecutorService.createLogger() = { inputStream: InputStream, isError: Boolean ->
        val logDestination : (String) -> Unit = if (isError) System.err::println else ::println
        submit(StreamGobbler(inputStream, logDestination))
        Unit
    }

    /**
     * Shows an error message and throws exception if the errorCode is not 0.
     */
    private fun Int.showErrorAndThrowExceptionIfFailed() {
        if (this == 0) return
        System.err.println("Something went wrong, please check the logs above!")
        throw RuntimeException("Something went wrong, please check the logs above!")
    }

    /**
     * Helper class to write to the given [inputStream]
     */
    private class StreamGobbler(private val inputStream: InputStream, private val consumer: (String) -> Unit) : Runnable {

        override fun run() {
            BufferedReader(InputStreamReader(inputStream)).lines().forEach(consumer)
        }
    }
}