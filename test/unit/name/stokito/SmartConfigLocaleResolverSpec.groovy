package name.stokito

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

import static java.util.Locale.*

@TestMixin(ControllerUnitTestMixin)
class SmartConfigLocaleResolverSpec extends Specification {
    static final Locale UNSUPPORTED_LOCALE = new Locale('xx')
    static final Locale ANY_LOCALE = new Locale('yy')
    static final Locale CONFIGURED_DEFAULT_LOCALE = new Locale('zz')
    static final Locale LOCALE_FROM_USER_REQUEST = new Locale('rr')
    static final Locale IGNORED_LOCALE = new Locale('ii')

    void 'resolveLocale() should return user requested locale if not configured: supportedLocales is empty list'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = []
        resolver.defaultLocale = null
        request.setPreferredLocales([UNSUPPORTED_LOCALE])
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == UNSUPPORTED_LOCALE
    }

    void 'resolveLocale() should return user requested locale if not configured: supportedLocales is null'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = null
        resolver.defaultLocale = null
        request.setPreferredLocales([UNSUPPORTED_LOCALE])
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == UNSUPPORTED_LOCALE
    }

    void 'localeIsSupported() should return false if supportedLocales are null'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = null
        expect:
        !resolver.localeIsSupported(ANY_LOCALE)
    }

    void 'resolveLocale() should return default locale if user requested unsupported one'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = null
        resolver.defaultLocale = CONFIGURED_DEFAULT_LOCALE
        request.setPreferredLocales([UNSUPPORTED_LOCALE])
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == CONFIGURED_DEFAULT_LOCALE
    }

    void 'resolveLocale() should return user requested locale if it supported'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [GERMANY]
        request.setPreferredLocales([GERMANY])
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == GERMANY
    }

    void 'resolveLocale() should return locale with same language if user requested locale that supported only partially by language'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [GERMAN]
        request.setPreferredLocales([GERMANY])
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == GERMAN
    }

    void 'findFirstPreferredSupportedLocaleByLanguage()'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [GERMAN, ITALY]
        when:
        Locale supportedLocaleWithSameLanguage = resolver.findFirstPreferredSupportedLocaleByLanguage([GERMANY])
        then:
        supportedLocaleWithSameLanguage == GERMAN
    }

    void 'findFirstPreferredSupportedLocaleByLanguageAndCountry()'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [GERMAN, GERMANY, ITALY]
        when:
        Locale supportedLocaleWithSameLanguage = resolver.findFirstPreferredSupportedLocaleByLanguageAndCountry([GERMANY])
        then:
        supportedLocaleWithSameLanguage == GERMANY
    }

    void 'resolveLocale() should return supported locale that is first matching with user requested locales'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [GERMANY]
        request.setPreferredLocales([UNSUPPORTED_LOCALE, GERMANY])
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == GERMANY
    }

    void 'findPreferredSupportedLocale() should return supported locale that is first matching with user requested locales'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [GERMANY]
        List<Locale> userPreferredLocales = [UNSUPPORTED_LOCALE, GERMANY]
        when:
        Locale preferredSupportedLocale = resolver.findFirstPreferredSupportedLocale(userPreferredLocales)
        then:
        preferredSupportedLocale == GERMANY
    }

    void 'findPreferredSupportedLocale()'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = configuredDefaultLocale
        expect:
        preferredSupportedLocale == resolver.findFirstPreferredSupportedLocale(userPreferredLocales)
        where:
        configuredDefaultLocale | userPreferredLocales | preferredSupportedLocale
        [ENGLISH, US, UK]       | [UK, ENGLISH]        | UK    // returned first preferred locale
        [ENGLISH, US, UK]       | [US, ENGLISH]        | US    // returned first preferred locale
        [ENGLISH, US, UK]       | [ENGLISH]            | ENGLISH
        [ENGLISH, US, UK]       | [CANADA, ENGLISH]    | ENGLISH // returned second preferred locale
        [ENGLISH, US, UK]       | [CANADA, US]         | US      // returned second preferred locale
        [ENGLISH, US, UK]       | [CANADA, UK]         | UK      // returned second preferred locale
        [ENGLISH, US, UK]       | [CANADA]             | ENGLISH // CANADA partially supported by language ENGLISH
    }

    @Unroll('#supportedLocales, #defaultLocale, #newLocale, #localeSavedToSession, #preferredSupportedLocale')
    void 'setLocale() with unsupported locale should set resolved supported locale'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = configuredSupportedLocales
        resolver.defaultLocale = configuredDefaultLocale
        request.setPreferredLocales(userPreferredLocales)
        when:
        resolver.setLocale(request, response, newLocale)
        localeSavedToSession == request.getSession().getAttribute(SmartConfigLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)
        // reset all to null to ensure that locale was got from session
        resolver.supportedLocales = null
        resolver.defaultLocale = null
        request.setPreferredLocales([IGNORED_LOCALE])
        then:
        preferredSupportedLocale == resolver.resolveLocale(request)
        where:
        configuredSupportedLocales | configuredDefaultLocale   | newLocale          | userPreferredLocales | localeSavedToSession      | preferredSupportedLocale
        [ENGLISH, US, UK]          | ANY_LOCALE                | UK                 | [ANY_LOCALE]         | UK                        | UK
        [ENGLISH, US, UK]          | ANY_LOCALE                | US                 | [ANY_LOCALE]         | US                        | US
        [ENGLISH, US, UK]          | ANY_LOCALE                | ENGLISH            | [ANY_LOCALE]         | ENGLISH                   | ENGLISH
        [ENGLISH, US, UK]          | ANY_LOCALE                | CANADA             | [ANY_LOCALE]         | ENGLISH                   | ENGLISH // newLocale partially supported by language
        [ENGLISH, US, UK]          | CONFIGURED_DEFAULT_LOCALE | UNSUPPORTED_LOCALE | [ANY_LOCALE]         | CONFIGURED_DEFAULT_LOCALE | CONFIGURED_DEFAULT_LOCALE      // newLocale unsupported, returned default language
        [ENGLISH, US, UK]          | null                      | UNSUPPORTED_LOCALE | [ANY_LOCALE]         | UNSUPPORTED_LOCALE        | UNSUPPORTED_LOCALE      // newLocale unsupported, but default isn't set, returned newLocale
        [ENGLISH, US, UK]          | ANY_LOCALE                | UNSUPPORTED_LOCALE | [US]                 | US                        | US // from request.locales
        [ENGLISH, US, UK]          | ANY_LOCALE                | UNSUPPORTED_LOCALE | [UK]                 | UK                        | UK // from request.locales
        [ENGLISH, US, UK]          | ANY_LOCALE                | UNSUPPORTED_LOCALE | [CANADA]             | ENGLISH                   | ENGLISH // from request.locales, supported by language
    }
}


