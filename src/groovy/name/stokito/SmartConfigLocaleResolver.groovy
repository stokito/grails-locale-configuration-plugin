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
        Locale selectedLocale = findPreferredSupportedLocale(request.locales.toList())
        if (!selectedLocale) {
            selectedLocale = determineDefaultLocale(request);
        }
        return selectedLocale

    }

    Locale findPreferredSupportedLocale(List<Locale> userPreferredLocales) {
        Locale localeDesiredByUser = userPreferredLocales[0]
        Locale preferredSupportedLocale
        if (localeIsSupported(localeDesiredByUser)) {
            preferredSupportedLocale = localeDesiredByUser
        } else {
            preferredSupportedLocale = findSupportedLocaleWithSameLanguage(localeDesiredByUser)
        }
        return preferredSupportedLocale
    }

    boolean localeIsSupported(Locale localeDesiredByUser) {
       return supportedLocales?.contains(localeDesiredByUser)
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
