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
        return determineBestLocale(request.locale, request.locales.toList())
    }

    @Override
    void setLocale(HttpServletRequest request, HttpServletResponse response, Locale newLocale) {
        List<Locale> requestLocales = [newLocale] + request.locales.toList()
        Locale selectedLocale = determineBestLocale(newLocale, requestLocales)
        super.setLocale(request, response, selectedLocale)
    }

    Locale determineBestLocale(Locale mainLocale, List<Locale> requestLocales) {
        return findFirstPreferredSupportedLocale(requestLocales) ?: (defaultLocale ?: mainLocale)
    }

    Locale findFirstPreferredSupportedLocale(List<Locale> requestLocales) {
        return findFirstSupportedLocaleByLanguageAndCountry(requestLocales) ?: findFirstSupportedLocaleByLanguage(requestLocales)
    }

    Locale findFirstSupportedLocaleByLanguageAndCountry(List<Locale> userPreferredLocales) {
        return userPreferredLocales.find { it in supportedLocales }
    }

    Locale findFirstSupportedLocaleByLanguage(List<Locale> userPreferredLocales) {
        return userPreferredLocales.find { it.language in supportedLocales*.language }
    }
}
