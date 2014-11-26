import grails.plugin.localeConfiguration.SmartConfigLocaleResolver

class LocaleConfigurationGrailsPlugin {
    String version = '1.1.1'
    String grailsVersion = '2.0 > *'
    String title = 'Grails Locale Configuration Plugin'
    def description = '''\
Smart locale resolver to enhance the handling of the Grails localization (i18n).
The plugin allows you to specify supported languages and a default language, e.g. if the requested language is not supported.
This plugin is intended to enhance the language support in Grails by detecting your users’ locale and selecting the most appropriate language for them.
You can specify a list of supported languages and choose which language to use if a client’s preferred language is not available.
'''
    String documentation = 'http://grails.org/plugin/locale-configuration'
    String license = 'APACHE'
    List developers = [
            [name: 'Sergey Ponomarev', email: 'stokito@gmail.com'],
            [name: 'Barry Norman', email: 'mail@barrynorman.de']
    ]
    def issueManagement = [system: 'GitHub', url: 'https://github.com/stokito/grails-locale-configuration-plugin/issues']
    def scm = [url: 'https://github.com/stokito/grails-locale-configuration-plugin']

    List loadAfter = ['i18n']

    def doWithSpring = {
        localeResolver(SmartConfigLocaleResolver) {
            supportedLocales = application.config.grails.plugin.localeConfiguration.supportedLocales ?: (application.config.grails.plugins.localeConfiguration.supportedLocales ?: [])
            defaultLocale = application.config.grails.plugin.localeConfiguration.defaultLocale ?: (application.config.grails.plugins.localeConfiguration.defaultLocale ?: null)
        }
    }
}
