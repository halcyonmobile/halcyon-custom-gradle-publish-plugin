# A gradle plugin to simpify publication to artifactory and bintray

## Purpose

The purpose of this plugin is to remove the deploy.gradle and deploy.aar.gradle copied files from our internal libraries 
and instead have one plugin which can be updated and fixes can be in place more quickly

## Setup

*Latest version:*![Latest release](https://img.shields.io/github/v/release/halcyonmobile/halcyon-custom-gradle-publish-plugin)

### Add plugin to your dependencies
- add your bintray access to your global gradle.properties:
bintray_username, bintray_password
- in your top-level build.gradle add the following setup to access halcyon gradle plugins and add the publish plugin:
```groovy
buildscript {
    /* ... */
    repositories {
        /* ... */
        jcenter()
    }
    dependencies {
        /* ... */
        classpath 'com.halcyonmobile.publish.artifactory-bintray:java-and-aar:<latest_version>'
    }
}
```

### Configure the plugin
- Create a new file deploy.gradle looking something like this:
```groovy
// RELEASING
// # 0:              update the libraryVersion
// # 1:              open terminal and run the following commands
// # 2 bintray:      ./gradlew publishToBintray
// # 3 bintray: make sure you add the published library to jcenter on the site

ext.libraryGroupId = 'library-group-id' // the dependency will result in something like implementation "com.halcyonmobile.<libraryGroupId>:<libraryArtifactId>:<libraryVersion>"
ext.libraryVersion = '0.1.0.2'
ext.bintray_source_url = "" // link to the sourcecode or left empty if it's not published to bintray
```

- Add the new file to your top-leve build.gradle at the end
```groovy
/*...*/
apply from: "./deploy.gradle"
``` 

- In your modules you want to publish add the following lines to the end of the build.gradle:
```groovy
project.ext.set("libraryArtifactId", "library-artifact-id")
apply plugin: 'com.halcyonmobile.plugin.publish.artifactory.jar-library' // or aar-library if it's an android module
```


And that's it now you can publish via ./gradlew publishToArtifactory or ./gradlew publishToBintray
You can also get help with ./gradlew howToPublish

## Configuration

### Bintray configuration
- By default the "maven" repo is used, to change this define in your deploy.gradle ext.bintray_repo_name
- By default Apache-2 license is used for bintray, if you wish to change, define in your deploy.gradle an ext.bintray_license


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
