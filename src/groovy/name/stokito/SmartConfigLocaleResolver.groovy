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
        Locale localeDesiredByUser = request.locale
        Locale selectedLocale
        if (localeIsSupported(localeDesiredByUser)) {
            selectedLocale = localeDesiredByUser
        } else {
            selectedLocale = findLocaleWithSameLanguage(localeDesiredByUser)
        }
        if (!selectedLocale) {
            selectedLocale = determineDefaultLocale(request);
        }
        return selectedLocale

    }

    boolean localeIsSupported(Locale localeDesiredByUser) {
       return supportedLocales?.contains(localeDesiredByUser)
    }

    private static Locale getLocaleSavedToSession(HttpServletRequest request) {
        Locale localeSavedToSession = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME)
        return localeSavedToSession
    }

    Locale findLocaleWithSameLanguage(Locale localeDesiredByUser) {
        Locale localeWithSameLanguage = supportedLocales.find({ it.language == localeDesiredByUser.language })
        return localeWithSameLanguage
    }

}
