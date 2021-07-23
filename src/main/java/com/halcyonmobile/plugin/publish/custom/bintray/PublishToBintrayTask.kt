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

package com.halcyonmobile.plugin.publish.custom.bintray

import com.halcyonmobile.plugin.publish.custom.ShellPublishTask
import com.halcyonmobile.plugin.publish.custom.artifactory.hasArtifactoryAccess

open class PublishToBintrayTask : ShellPublishTask() {
    override val actualPublishTask: String get() = "bintrayUpload"

    override fun verifyConfiguration(): Boolean {
        if (!hasArtifactoryAccess) {
            System.err.println("Project doesn't have bintray access, please provide the following configurations:")
            System.err.println("required configurations username: $BINTRAY_USERNAME_KEY")
            System.err.println("required configurations password: $BINTRAY_PASSWORD_KEY")
            System.err.println("configuration need to be set in the \$HOME/.bash_profile \"export KEY=value\" format")
            return false
        }
        return true
    }
}