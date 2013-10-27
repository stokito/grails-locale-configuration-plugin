grails.project.work.dir = 'target'

grails.project.repos.default = 'bintray-stokito-maven-grails-locale-configuration-plugin'
grails.project.repos.'bintray-stokito-maven-grails-locale-configuration-plugin'.url = 'https://api.bintray.com/maven/stokito/maven/grails-locale-configuration-plugin'
grails.project.repos.'bintray-stokito-maven-grails-locale-configuration-plugin'.type = 'maven'
grails.project.repos.'bintray-stokito-maven-grails-locale-configuration-plugin'.portal = 'stokitoBintray'

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
    }

    plugins {
        build ':release:3.0.1', ':rest-client-builder:1.0.3', {
            export = false
        }
    }
}
