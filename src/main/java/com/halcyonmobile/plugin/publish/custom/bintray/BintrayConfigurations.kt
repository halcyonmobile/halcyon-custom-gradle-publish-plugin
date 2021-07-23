package com.halcyonmobile.plugin.publish.custom.bintray

import org.gradle.api.Project

private const val BINTRAY_SOURCE_URL_KEY = "bintraySourceUrl"
private const val BINTRAY_REPO_NAME = "bintrayRepoName"
private const val DEFAULT_BINTRAY_REPO_NAME = "maven"
private const val BINTRAY_LICENSE = "bintrayLicense"
private const val DEFAULT_BINTRAY_LICENSE = "Apache-2.0"
const val BINTRAY_USERNAME_KEY = "bintray_username"
const val BINTRAY_PASSWORD_KEY = "bintray_password"

val hasBintrayAccess: Boolean get() = System.getenv(BINTRAY_USERNAME_KEY) != null

val Project.bintrayRepo
    get() : String =
        if (!hasProperty(BINTRAY_REPO_NAME)) DEFAULT_BINTRAY_REPO_NAME else property(BINTRAY_REPO_NAME) as String

val Project.bintrayLicense
    get() : String =
        if (!hasProperty(BINTRAY_LICENSE)) DEFAULT_BINTRAY_LICENSE else property(BINTRAY_LICENSE) as String

val bintrayUserName: String
    get() = System.getenv(BINTRAY_USERNAME_KEY) ?: throw IllegalStateException()

val bintrayPassword: String
    get() = System.getenv(BINTRAY_PASSWORD_KEY) ?: throw IllegalStateException()

val Project.bintrayVcsUrl: String
    get() : String {
        if (!hasProperty(BINTRAY_SOURCE_URL_KEY)) {
            throw IllegalStateException("$BINTRAY_SOURCE_URL_KEY is not defined. Make sure to define it in your root build.gradle. This is the url for the source code, most probably github.")
        }
        return property(BINTRAY_SOURCE_URL_KEY) as String
    }
