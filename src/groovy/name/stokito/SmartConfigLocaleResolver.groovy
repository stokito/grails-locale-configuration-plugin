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
        Locale selectedLocale = findFirstPreferredSupportedLocale(request.locales.toList())
        if (selectedLocale) {
            return selectedLocale
        }
        return super.determineDefaultLocale(request);
    }
}
