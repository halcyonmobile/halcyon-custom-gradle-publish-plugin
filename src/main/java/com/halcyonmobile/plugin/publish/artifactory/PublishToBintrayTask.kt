package com.halcyonmobile.plugin.publish.artifactory

open class PublishToBintrayTask : ShellPublishTask() {
    override val actualPublishTask: String get() = "bintrayUpload"
}