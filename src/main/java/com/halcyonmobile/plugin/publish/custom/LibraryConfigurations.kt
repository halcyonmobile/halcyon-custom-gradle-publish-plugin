/*
 * Copyright (c) 2020 Halcyon Mobile.
 * https://www.halcyonmobile.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.halcyonmobile.plugin.publish.custom

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

val Project.libraryLicenseName
    get(): String {
        return if (!project.hasProperty(LIBRARY_LICENSE_NAME)) {
            println("$LIBRARY_LICENSE_NAME is not defined. Using default: $DEFAULT_LIBRARY_LICENSE_NAME")
            DEFAULT_LIBRARY_LICENSE_NAME
        } else {
            property(LIBRARY_LICENSE_NAME) as? String? ?: throw IllegalStateException("${property(LIBRARY_LICENSE_NAME)} cant be accessed")
        }
    }

val Project.libraryLicenseUrl
    get(): String {
        return if (!project.hasProperty(LIBRARY_LICENSE_URL)) {
            println("$LIBRARY_LICENSE_URL is not defined. Using default: $DEFAULT_LIBRARY_LICENSE_URL")
            DEFAULT_LIBRARY_LICENSE_URL
        } else {
            property(LIBRARY_LICENSE_URL) as? String? ?: throw IllegalStateException("${property(LIBRARY_LICENSE_NAME)} cant be accessed")
        }
    }

private const val LIBRARY_GROUP_ID_KEY = "libraryGroupId"
private const val LIBRARY_VERSION = "libraryVersion"
private const val LIBRARY_ARTIFACT_ID_KEY = "libraryArtifactId"
private const val LIBRARY_LICENSE_NAME = "libraryLicenseName"
private const val DEFAULT_LIBRARY_LICENSE_NAME = "The Apache License, Version 2.0"
private const val LIBRARY_LICENSE_URL = "libraryLicenseUrl"
private const val DEFAULT_LIBRARY_LICENSE_URL = "http://www.apache.org/licenses/LICENSE-2.0.txt"