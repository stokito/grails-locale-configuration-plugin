package name.stokito

import org.springframework.web.servlet.i18n.SessionLocaleResolver

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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

    @Override
    protected Locale determineDefaultLocale(HttpServletRequest request) {
        return determineBestLocale(request.locales.toList())
    }

    def Locale determineBestLocale(List<Locale> requestedLocales) {
        Locale selectedLocale = findFirstPreferredSupportedLocale(requestedLocales)
        if (!selectedLocale) {
            Locale mainRequestedLocale = requestedLocales[0]
            selectedLocale = defaultLocale ?: mainRequestedLocale
        }
        return selectedLocale
    }

    @Override
    void setLocale(HttpServletRequest request, HttpServletResponse response, Locale newLocale) {
        List<Locale> userPreferredLocales = [newLocale]
        userPreferredLocales.addAll(request.locales.toList())
        Locale selectedLocale = determineBestLocale(userPreferredLocales)
        super.setLocale(request, response, selectedLocale)
    }
}
