package name.stokito

import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.web.util.WebUtils

import javax.servlet.http.HttpServletRequest

/**
 * Locale resolver that can limit choice user preferred language.
 * You can define bean of this resolver in resouces.groovy:
 *
 *  localeResolver(SmartConfigLocaleResolver) {
 *      supportedLocales = application.config.supportedLocales ?: []
 *      defaultLocale = application.config.defaultLocale ?: null
 *  }
 *
 */
class SmartConfigLocaleResolver extends SessionLocaleResolver {

    List<Locale> supportedLocales

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale localeSavedToSession = getLocaleSavedToSession(request);
        if (localeSavedToSession) {
            return localeSavedToSession
        }
        Locale selectedLocale = findFirstPreferredSupportedLocale(request.locales.toList())
        if (!selectedLocale) {
            selectedLocale = determineDefaultLocale(request);
        }
        return selectedLocale

    }

    Locale findFirstPreferredSupportedLocale(List<Locale> userPreferredLocales) {
        Locale preferredSupportedLocale = userPreferredLocales.find( { preferredLocale -> localeIsSupported(preferredLocale) })
        if (!preferredSupportedLocale) {
            preferredSupportedLocale = userPreferredLocales.find( { preferredLocale -> localeIsSupportedByLanguage(preferredLocale) })
        }
        return preferredSupportedLocale
    }

    boolean localeIsSupported(Locale localeDesiredByUser) {
       return supportedLocales?.contains(localeDesiredByUser)
    }

    boolean localeIsSupportedByLanguage(Locale localeDesiredByUser) {
       return supportedLocales*.language?.contains(localeDesiredByUser.language)
    }

    private static Locale getLocaleSavedToSession(HttpServletRequest request) {
        Locale localeSavedToSession = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME)
        return localeSavedToSession
    }

    Locale findSupportedLocaleWithSameLanguage(Locale localeDesiredByUser) {
        Locale localeWithSameLanguage = supportedLocales.find({ it.language == localeDesiredByUser.language })
        return localeWithSameLanguage
    }

}
