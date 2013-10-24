package name.stokito

import org.springframework.web.servlet.i18n.SessionLocaleResolver

import javax.servlet.http.HttpServletRequest

class SmartConfigLocaleResolver extends SessionLocaleResolver {

    List<Locale> supportedLocales

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale localeDesiredByUser = super.resolveLocale(request)
        localeDesiredByUser
        // Сначала ищем такую же локаль, если не нашли то локаль с тем же языком, если не нашли то по умолчанию английский
        Locale selectedLocale
        if (supportedLocales.contains(localeDesiredByUser)) {
            selectedLocale = localeDesiredByUser
        } else {
            selectedLocale = findLocaleWithSameLanguage(localeDesiredByUser, supportedLocales)
        }
        if (!selectedLocale) {
            selectedLocale = determineDefaultLocale(request);
        }
        return selectedLocale

    }

    private static Locale findLocaleWithSameLanguage(Locale localeDesiredByUser, List<Locale> supportedLocales) {
        Locale localeWithSameLanguage = supportedLocales.find({ it.language == localeDesiredByUser.language })
        return localeWithSameLanguage
    }

}
