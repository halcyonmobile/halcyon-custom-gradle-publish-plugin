# A gradle plugin to simplify publication to artifactory, bintray, github

## Purpose

The purpose of this plugin is to remove the deploy.gradle and deploy.aar.gradle copied files from our internal libraries 
and instead have one plugin which can be updated and fixes can be in place more quickly

## Setup

*Latest version:*![Latest release](https://img.shields.io/github/v/release/halcyonmobile/halcyon-custom-gradle-publish-plugin)

### Set credentials

- add your github access to your global .bash_profile:
GITHUB_USERNAME, GITHUB_TOKEN
```bash 
file: $HOME/.bash_profile

#..
export GITHUB_USERNAME="yourUserName"
export GITHUB_TOKEN="yourToken" # can be generated in GitHub / settings / developer settings / Personal Access Token / Generate new token. and check read packages
# see: https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token
#..
```

### Add plugin to your dependencies

- in your top-level build.gradle add the following setup to access halcyon gradle plugins and add the publish plugin:
```groovy
buildscript {
    /* ... */
    repositories {
        /* ... */
        maven {url "https://plugins.gradle.org/m2/" }
        maven {
            url "https://maven.pkg.github.com/halcyonmobile/halcyon-custom-gradle-publish-plugin"
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    dependencies {
        /* ... */
        classpath 'com.halcyonmobile.publish.custom:java-and-aar:<latest_version>'
    }
}
```

### Configure the plugin

- Create a new file deploy.gradle looking something like this:
```groovy
project.ext.set("libraryGroupId","example-library-group-id") // the dependency will result in something like implementation "com.halcyonmobile.<example-library-group-id>:<example-library-artifact-id>:<0.1.0.2>"
project.ext.set("libraryVersion","0.1.0.2")
project.ext.set("githubPackagePath","halcyonmobile/halcyon-custom-gradle-publish-plugin") // this is the path of the github package, comes from "https://github.com/halcyonmobile/halcyon-custom-gradle-publish-plugin/"
```

- Add the new file to your top-leve build.gradle at the end
```groovy
/*...*/
apply from: "./deploy.gradle"
``` 

- In your modules you want to publish add the following lines to the end of the build.gradle:
```groovy
project.ext.set("libraryArtifactId", "example-library-artifact-id")
apply plugin: 'com.halcyonmobile.plugin.publish.custom.jar-library' // or aar-library if it's an android module
```

And that's it now you can publish via `./gradlew publishtoGitHub`

Artfactory and Bintray are deprecated, kept only for reference: `./gradlew publishToArtifactory`, `./gradlew publishToBintray,`

You can also get help with ./gradlew howToPublish

## GitHub Actions Integration

As mentioned above, you will need to add the credentials to the .bash_profile and in the top level gradle file.

Additionally you can generate a workflow for GitHub which automatically publishes a package, when you create a new release on the GitHub website:

1. Configure the project as for releasing to GitHub
2. `./gradlew generatePublishScriptForGitHubActions`
3. verify the file is generated under project/.github/workflows/release-package.yml and don't forget to add it to git.

Now whenever you create a Release on GitHub website this action will create a new package.

For help run `./gradlew howToIntegrateGitHubActions`

## Example

This project uses it's previous version to publish itself to GitHub Packages, so the project's build.gradle and self-deploy.gradle is in itself an example.

<h1 id="license">License :page_facing_up:</h1>

Copyright (c) 2020 Halcyon Mobile.
> https://www.halcyonmobile.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

> http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
