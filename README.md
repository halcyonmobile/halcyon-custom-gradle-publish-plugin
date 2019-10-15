# A gradle plugin to simpify publication to artifactory and bintray

## Purpose

The purpose of this plugin is to remove the deploy.gradle and deploy.aar.gradle copied files from our internal libraries 
and instead have one plugin which can be updated and fixes can be in place more quickly

## Setup

latest version is 0.1.0.1

### Add artifactory and plugin to your dependencies
- add your bintray and artifactory access to your gradle.properties:
artifactory_username, artifactory_password, bintray_username, bintray_password
- If you don't want to publish to bintray feel free to left those out
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
// # 2 artifactory:  ./gradlew publishToArtifactory
// # 2 bintray:      ./gradlew publishToBintray
// # 3 bintray: make sure you add the published library to jcenter on the site

ext.libraryGroupId = 'library-group-id' // the dependency will result in something like implementation "com.halcyonmobile.<libraryGroupId>:<libraryArtifactId>:<libraryVersion>"
ext.libraryVersion = '0.1.0.1'
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

### Artifactory
- if you are not releasing to libs-release-local, you can change it by defining in your deploy.gradle an ext.artifactory_repo_key