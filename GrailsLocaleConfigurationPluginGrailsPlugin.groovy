import name.stokito.SmartConfigLocaleResolver

class GrailsLocaleConfigurationPluginGrailsPlugin {
    String groupId = 'name.stokito'
    String version = '0.2'
    String grailsVersion = '2.3 > *'
    String title = 'Grails Locale Configuration Plugin'
    String author = 'Sergey Ponomarev'
    String authorEmail = 'stokito@gmail.com'
    def description = '''\
Smart locale resolver to enhance the handling of the Grails localization (i18n).
The plugin allows you to specify supported languages and a default language, e.g. if the requested language is not supported.
'''
    String documentation = 'http://grails.org/plugin/grails-locale-configuration-plugin' //TODO http://grails.org/plugin/grails-locale-configuration-plugin
    String license = 'APACHE'
    List developers = [
            [name: 'Sergey Ponomarev', email: 'stokito@gmail.com'],
            [name: 'Barry Norman', email: 'mail@barrynorman.de']
    ]
    def issueManagement = [system: 'GitHub', url: 'https://github.com/stokito/grails-locale-configuration-plugin/issues']
    def scm = [url: 'https://github.com/stokito/grails-locale-configuration-plugin'] //TODO http://svn.codehaus.org/grails-plugins/

    List loadAfter = ['i18n']
    def doWithSpring = {
        localeResolver(SmartConfigLocaleResolver) {
            supportedLocales = application.config.name.stokito.smartLocaleResolver.supportedLocales ?: []
            defaultLocale = application.config.name.stokito.smartLocaleResolver.defaultLocale ?: null
        }
    }

}
