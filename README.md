Grails Locale Configuration Plugin
==================================

[Grails Locale Configuration Plugin] (https://github.com/stokito/grails-locale-configuration-plugin) enhances the handling of Grails localization (i18n).

This plugin is intended to enhance the language support in Grails by detecting your users’ locale and selecting the most appropriate language for them.
You can specify a list of supported languages and choose which language to use if a client’s preferred language is not available.

For example, if your site supports only English, Russian and German, you can send users with other locales to the default language (English).

Installation
================
Please add the following repository and dependency to your BuildConfig.groovy:

    grails.project.dependency.resolution = {
        ...
        plugins {
            ...
            compile ':locale-configuration:1.0'
            ...
        }
        ...
    }

Configuration
=============
Add the following lines to your Config.groovy.

    // order is matters!
    grails.plugins.localeConfiguration.supportedLocales = [Locale.GERMAN, Locale.ENGLISH]
    grails.plugins.localeConfiguration.defaultLocale = Locale.ENGLISH

Locales priority
================
1. Language Param in the URL (request), e.g. http://www.example.net?lang=en
2. First match of the locales from the HTTP Accept-Language header
3. Default locale (defaultLocale option in Config.groovy)

NOTE: This order is important and cannot be changed. You may want to provide a language selector, so the lang param should have the highest priority.

[Take a look into specification for more details](/test/unit/name/stokito/SmartConfigLocaleResolverSpec.groovy)

Running application example
===========================
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
 * [Sergey Ponomarev](http://stokito.wordpress.com/)

Thanks to [Barry Norman](https://github.com/jigsawIV) for contribution!
