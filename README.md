Grails Locale Configuration Plugin
==================================

This plugin intended to control the language support in Grails. Your site may support only few languages.
You can allow only supported languages. For example: English, Russian and German, and all other languages should be forced to English.

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


