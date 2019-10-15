package com.halcyonmobile.plugin.publish.artifactory

import org.gradle.api.Project

val Project.nakedLibraryGroupId
    get() : String {
        if (!project.hasProperty(LIBRARY_GROUP_ID_KEY)) {
            System.err.println("$LIBRARY_GROUP_ID_KEY is not defined. Make sure to define it in your root build.gradle. This will be the base name of the library.\nYou have to define it as ext.libraryGroupId = \"name\".\nWill be used as: `implementation com.halcyonmobile.<libraryGroupId>:<libraryArtifactId>:<libraryVersion>`")
        }
        return property(LIBRARY_GROUP_ID_KEY) as? String? ?: throw IllegalStateException()
    }

val Project.libraryGroupId get() = "com.halcyonmobile.$nakedLibraryGroupId"

val Project.libraryArtifactId
    get() : String {
        if (!project.hasProperty(LIBRARY_ARTIFACT_ID_KEY)) {
            System.err.println("$LIBRARY_ARTIFACT_ID_KEY is not defined. Make sure to define it in your build.gradle of the module you wish to publish. This will be the name of the library.\nYou have to define it as project.ext.libraryArtifactId = \"name\".\nWill be used as: `implementation com.halcyonmobile.<libraryGroupId>:<libraryArtifactId>:<libraryVersion>`")
        }
        return property(LIBRARY_ARTIFACT_ID_KEY) as? String? ?: throw  IllegalStateException()
    }
val Project.libraryVersion
    get() : String {
        if (!project.hasProperty(LIBRARY_VERSION)) {
            System.err.println("$LIBRARY_VERSION is not defined. Make sure to define it in your root build.gradle. This will be the base name of the library.\nYou have to define it as ext.libraryVersion = \"0.1.0.1\".\nWill be used as: `implementation com.halcyonmobile.<libraryGroupId>:<libraryArtifactId>:<libraryVersion>`")
        }
        return property(LIBRARY_VERSION) as? String? ?: throw IllegalStateException()
    }
val Project.bintrayRepo
    get() : String =
        if (!project.hasProperty(BINTRAY_REPO_NAME)) DEFAULT_BINTRAY_REPO_NAME else property(BINTRAY_REPO_NAME) as String

val Project.bintrayLicense
    get() : String =
        if (!project.hasProperty(BINTRAY_LICENSE)) DEFAULT_BINTRAY_LICENSE else property(BINTRAY_LICENSE) as String

val Project.artifactoryUserName: String
    get() = findProperty(ARTIFACTORY_USERNAME_KEY) as? String? ?: throw IllegalStateException()

val Project.artifactoryPassword: String
    get() = findProperty(ARTIFACTORY_PASSWORD_KEY) as? String? ?: throw IllegalStateException()

val Project.hasBintrayAccess: Boolean get() = hasProperty(BINTRAY_USERNAME_KEY)

val Project.bintrayUserName: String
    get() = findProperty(BINTRAY_USERNAME_KEY) as? String? ?: throw IllegalStateException()

val Project.bintrayPassword: String
    get() = findProperty(BINTRAY_PASSWORD_KEY) as? String? ?: throw IllegalStateException()

val Project.repoKey: String
    get() = if (hasProperty(ARTIFACTORY_REPO_KEY)) findProperty(ARTIFACTORY_REPO_KEY) as String else ARTIFACTORY_DEFAULT_REPO_KEY

val Project.bintrayVcsUrl: String
    get() : String {
        if (!project.hasProperty(BINTRAY_SOURCE_URL_KEY)) {
            System.err.println("$BINTRAY_SOURCE_URL_KEY is not defined. Make sure to define it in your root build.gradle. This is the url for the source code, most probably github.")
        }
        return findProperty(BINTRAY_SOURCE_URL_KEY) as String
    }

private const val BINTRAY_SOURCE_URL_KEY = "bintray_source_url"
private const val ARTIFACTORY_USERNAME_KEY = "artifactory_username"
private const val ARTIFACTORY_PASSWORD_KEY = "artifactory_password"
private const val LIBRARY_GROUP_ID_KEY = "libraryGroupId"
private const val LIBRARY_VERSION = "libraryVersion"
private const val LIBRARY_ARTIFACT_ID_KEY = "libraryArtifactId"
private const val BINTRAY_REPO_NAME = "bintray_repo_name"
private const val DEFAULT_BINTRAY_REPO_NAME = "maven"
private const val BINTRAY_LICENSE = "BINTRAY_LICENSE"
private const val DEFAULT_BINTRAY_LICENSE = "Apache-2.0"
const val BINTRAY_USERNAME_KEY = "bintray_username"
const val BINTRAY_PASSWORD_KEY = "bintray_password"
const val ARTIFACTORY_URL = "https://artifactory.build.halcyonmobile.com/artifactory"
const val ARTIFACTORY_REPO_KEY = "artifactory_repo_key"
const val ARTIFACTORY_DEFAULT_REPO_KEY = "libs-release-local"