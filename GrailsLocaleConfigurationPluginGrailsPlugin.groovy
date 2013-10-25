import name.stokito.SmartConfigLocaleResolver

class GrailsLocaleConfigurationPluginGrailsPlugin {
    String groupId = 'name.stokito'
    String version = '0.2'
    String grailsVersion = '2.3 > *'
    String title = 'Grails Locale Configuration Plugin'
    String author = 'Sergey Ponomarev'
    String authorEmail = 'stokito@gmail.com'
    def description = '''\
This plugin intended to control the language support in Grails. Your site may support only few languages.
You can allow only supported languages. For example: English, Russian and German, and all other languages should be forced to English.
'''
    String documentation = 'http://grails.org/plugin/grails-locale-configuration-plugin' //TODO http://grails.org/plugin/grails-locale-configuration-plugin
    String license = 'APACHE'
    List developers = [
            [name: 'Sergey Ponomarev', email: 'stokito@gmail.com'],
            [name: 'Barry Norman', email: 'mail@barrynorman']
    ]
    def issueManagement = [system: 'GitHub', url: 'https://github.com/stokito/grails-locale-configuration-plugin/issues']
    def scm = [url: 'https://github.com/stokito/grails-locale-configuration-plugin'] //TODO http://svn.codehaus.org/grails-plugins/

    List loadAfter = ['i18n']
    def doWithSpring = {
        localeResolver(SmartConfigLocaleResolver) {
            supportedLocales = application.config.supportedLocales ?: []
            defaultLocale = application.config.defaultLocale ?: null
        }
    }

}
