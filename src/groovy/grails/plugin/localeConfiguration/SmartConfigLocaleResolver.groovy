package grails.plugin.localeConfiguration

import org.springframework.web.servlet.i18n.SessionLocaleResolver

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *
 * Locale resolver that can limit choice user preferred language.
 * You can define bean of this resolver in resources.groovy:
 *
 *  localeResolver(SmartConfigLocaleResolver) {
 *      supportedLocales = application.config.grails.plugin.localeConfiguration.supportedLocales ?: []
 *      defaultLocale = application.config.grails.plugin.localeConfiguration.defaultLocale ?: null
 *  }
 *
 */
class SmartConfigLocaleResolver extends SessionLocaleResolver {
    LinkedHashSet<Locale> supportedLocales

    @Override
    protected Locale determineDefaultLocale(HttpServletRequest request) {
        return determineBestLocale(request.locales.toList())
    }

    @Override
    void setLocale(HttpServletRequest request, HttpServletResponse response, Locale newLocale) {
        List<Locale> requestLocales = [newLocale] + request.locales.toList()
        newLocale = determineBestLocale(requestLocales) ?: defaultLocale
        super.setLocale(request, response, newLocale)
    }

    Locale determineBestLocale(List<Locale> requestLocales) {
        return findFirstSupportedLocale(requestLocales)
    }

    Locale findFirstSupportedLocale(List<Locale> requestLocales) {
        return findFirstSupportedLocaleByLanguageAndCountry(requestLocales) ?: findFirstSupportedLocaleByLanguage(requestLocales)
    }

    Locale findFirstSupportedLocaleByLanguageAndCountry(List<Locale> requestLocales) {
        return requestLocales.find { it in supportedLocales }
    }

    Locale findFirstSupportedLocaleByLanguage(List<Locale> requestLocales) {
        return requestLocales.find { it.language in supportedLocales*.language }
    }
}
