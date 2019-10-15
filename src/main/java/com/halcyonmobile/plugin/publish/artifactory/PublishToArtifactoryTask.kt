package com.halcyonmobile.plugin.publish.artifactory

open class PublishToArtifactoryTask : ShellPublishTask() {
    override val actualPublishTask: String get() = "artifactoryPublish"
}