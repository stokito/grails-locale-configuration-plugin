package grails.plugin.localeConfiguration

import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.util.WebUtils

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
class SmartConfigLocaleResolver implements LocaleResolver {
    static final LOCALE_SESSION_ATTRIBUTE_NAME =  SmartConfigLocaleResolver.class.name + '.LOCALE';
    List<Locale> supportedLocales
    Locale defaultLocale


    @Override
    void setLocale(HttpServletRequest request, HttpServletResponse response, Locale newLocale) {
        if (newLocale) {
            newLocale = findFirstSupportedLocale([newLocale])
        }
        WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME, newLocale);
    }

    @Override
    Locale resolveLocale(HttpServletRequest request) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME)
        if (!locale) {
            locale = findFirstSupportedLocale(request.locales.toList()) ?: (defaultLocale ?: Locale.getDefault())
        }
        return locale;
    }

    Locale findFirstSupportedLocale(List<Locale> requestLocales) {
        return findFirstSupportedLocaleByLanguageAndCountry(requestLocales) ?: findFirstSupportedLocaleByLanguage(requestLocales)
    }

    Locale findFirstSupportedLocaleByLanguageAndCountry(List<Locale> requestLocales) {
        return requestLocales.find { it in supportedLocales }
    }

    Locale findFirstSupportedLocaleByLanguage(List<Locale> requestLocales) {
        for (Locale preferredLocale : requestLocales) {
            Locale supportedByLanguageLocale = supportedLocales?.find { it.language == preferredLocale.language }
            if (supportedByLanguageLocale) {
                return supportedByLanguageLocale
            }
        }
        return null
    }
}
