package name.stokito

import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.web.util.WebUtils

import javax.servlet.http.HttpServletRequest

/**
 *
 * Locale resolver that can limit choice user preferred language.
 * You can define bean of this resolver in resources.groovy:
 *
 *  localeResolver(SmartConfigLocaleResolver) {
 *      supportedLocales = application.config.name.stokito.smartLocaleResolver.supportedLocales ?: []
 *      defaultLocale = application.config.name.stokito.smartLocaleResolver.defaultLocale ?: null
 *  }
 *
 */
class SmartConfigLocaleResolver extends SessionLocaleResolver {

    List<Locale> supportedLocales

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale localeSavedToSession = getLocaleSavedToSession(request);
        if (localeSavedToSession && localeIsSupported(localeSavedToSession)) {
            return localeSavedToSession
        }
        Locale selectedLocale = findFirstPreferredSupportedLocale(request.locales.toList())
        if (!selectedLocale) {
            selectedLocale = determineDefaultLocale(request);
        }
        return selectedLocale

    }

    Locale findFirstPreferredSupportedLocale(List<Locale> userPreferredLocales) {
        Locale preferredSupportedLocale = findFirstPreferredSupportedLocaleByLanguageAndCountry(userPreferredLocales)
        if (!preferredSupportedLocale) {
            preferredSupportedLocale = findFirstPreferredSupportedLocaleByLanguage(userPreferredLocales)
        }
        return preferredSupportedLocale
    }

    Locale findFirstPreferredSupportedLocaleByLanguage(List<Locale> userPreferredLocales) {
        for (Locale preferredLocale : userPreferredLocales) {
            Locale supportedByLanguageLocale = supportedLocales?.find({ supportedLocale -> supportedLocale.language == preferredLocale.language })
            if (supportedByLanguageLocale) {
                return supportedByLanguageLocale
            }
        }
        return null
    }

    Locale findFirstPreferredSupportedLocaleByLanguageAndCountry(List<Locale> userPreferredLocales) {
        userPreferredLocales.find({ preferredLocale -> localeIsSupported(preferredLocale) })
    }

    boolean localeIsSupported(Locale localeDesiredByUser) {
        return supportedLocales?.contains(localeDesiredByUser)
    }

    private static Locale getLocaleSavedToSession(HttpServletRequest request) {
        Locale localeSavedToSession = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME)
        return localeSavedToSession
    }

}
