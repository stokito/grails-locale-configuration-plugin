Grails Locale Configuration Plugin
==================================

[Grails Locale Configuration Plugin] (https://github.com/stokito/grails-locale-configuration-plugin) to enhance the handling of the Grails localization (i18n).

The plugin allows you to define supported languages and a default language, e.g. if the requested language (lang parameter or HTTP-Accept-Language-Header) is not supported.

Installation
================
Please add the following repository and dependency to your BuildConfig.groovy:

    grails.project.dependency.resolution = {

        repositories {
            ...
            mavenRepo 'http://dl.bintray.com/stokito/maven'
        }

        ...

        plugins {
            ...
            compile 'name.stokito:grails-locale-configuration-plugin:0.2'
        }

    }

Configuration
=============
By default English and German are defined as supported locales and English is default.
If you want to change it, add the following lines to your Config.groovy.

NOTE: The default locale should also be in the list of the supported locales...

    name.stokito.smartLocaleResolver.supportedLocales = [Locale.ENGLISH, Locale.GERMAN]
    name.stokito.smartLocaleResolver.defaultLocale = Locale.ENGLISH

Locales priority
================
1. Language Param in the URL (request), e.g. http://www.example.net?lang=en
2. First locale from the HTTP-Accept-Language-Header
3. Default locale (Config.groovy)

NOTE: This order is important and not changeable to support switching languages (e.g. language selector)

Running application example
================================
Take a look at the demo to see the plugin in action: [Grails Locale Configuration Plugin demo application] (https://github.com/stokito/grails-locale-configuration-plugin-demo)

NOTE: If you want to test your locales (languages), just add the locales to the Config.groovy as descriped in 'Configuration'.

Run demo, like every other Grails application
    grails run-app

Application can be accessed by URL http://localhost:8080/

![Screenshot of test stand](/screenshot.png "Screenshot of test stand")

Support
=======
For any questions feel free to contact me:

 * stokito@gmail.com
 * skype: stokito
 * https://linkedin.com/in/stokito
 * Sergey Ponomarev

Thanks to Barry Norman for contribution.