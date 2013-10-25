Grails Locale Configuration Plugin
==================================

[Grails Locale Configuration Plugin] (https://github.com/stokito/grails-locale-configuration-plugin) intended to control the language support in Grails.

You can allow only supported languages. For example: English, Russian and German, and all other languages should be forced to English.

Installation
================
You need change BuildConfig.groovy and add new repository and install plugin dependency:

    grails.project.dependency.resolution = {
        repositories {
            ...
            mavenRepo 'http://dl.bintray.com/stokito/maven'
        }
        plugins {
            ...
            compile 'name.stokito:grails-locale-configuration-plugin:0.2'
        }
    }

Configuration
=============
Just create two options in your Config.groovy:

    supportedLocales = [Locale.ENGLISH, new Locale('RU')] // Locales that user can choice
    defaultLocale=Locale.GERMAN // Default locale if user choice unsupported locale

Locales priority
================
1. lang param in request http://localhost:8080/?lang=ru_Ru
2. First locale from Accept-language header that is configured in user browser.
3. defaultLocale from Config.groovy

Running application example
================================
You can watch [Grails Locale Configuration Plugin demo application] (https://github.com/stokito/grails-locale-configuration-plugin-demo)

To run this demo just type to command line
    grails run-app

Then application can be accessed by URL http://localhost:8080/

![Screenshot of test stand](/screenshot.png "Screenshot of test stand")

All important changes in source code are marked with comment WATCHME. Just run search by files of 'WATCHME':

![Screenshot WATCHME in sources](/screenshot_watchme_in_sources.png "Screenshot WATCHME in sources")


Support
=======
For any questions feel free to contact me:

 * stokito@gmail.com
 * skype: stokito
 * https://linkedin.com/in/stokito
 * Sergey Ponomarev

Also thanks to Barry Norman for contribution.