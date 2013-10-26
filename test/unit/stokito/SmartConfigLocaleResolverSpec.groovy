package stokito

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import name.stokito.SmartConfigLocaleResolver
import spock.lang.Specification
import spock.lang.Unroll

@TestMixin(ControllerUnitTestMixin)
class SmartConfigLocaleResolverSpec extends Specification {
    static final Locale UNSUPPORTED_LOCALE = new Locale('xx')
    static final Locale ANY_LOCALE = new Locale('yy')
    static final Locale CONFIGURED_DEFAULT_LOCALE = new Locale('zz')
    static final Locale LOCALE_FROM_USER_REQUEST = new Locale('rr')

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
        resolver.supportedLocales = [Locale.GERMANY]
        request.setPreferredLocales([Locale.GERMANY])
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == Locale.GERMANY
    }

    void 'resolveLocale() should return locale with same language if user requested locale that supported only partially by language'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [Locale.GERMAN]
        request.setPreferredLocales([Locale.GERMANY])
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == Locale.GERMAN
    }

    void 'findFirstPreferredSupportedLocaleByLanguage()'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [Locale.GERMAN, Locale.ITALY]
        when:
        Locale supportedLocaleWithSameLanguage = resolver.findFirstPreferredSupportedLocaleByLanguage([Locale.GERMANY])
        then:
        supportedLocaleWithSameLanguage == Locale.GERMAN
    }

    void 'findFirstPreferredSupportedLocaleByLanguageAndCountry()'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [Locale.GERMAN, Locale.GERMANY, Locale.ITALY]
        when:
        Locale supportedLocaleWithSameLanguage = resolver.findFirstPreferredSupportedLocaleByLanguageAndCountry([Locale.GERMANY])
        then:
        supportedLocaleWithSameLanguage == Locale.GERMANY
    }

    void 'resolveLocale() should return supported locale that is first matching with user requested locales'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [Locale.GERMANY]
        request.setPreferredLocales([UNSUPPORTED_LOCALE, Locale.GERMANY])
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == Locale.GERMANY
    }

    void 'findPreferredSupportedLocale() should return supported locale that is first matching with user requested locales'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [Locale.GERMANY]
        List<Locale> userPreferredLocales = [UNSUPPORTED_LOCALE, Locale.GERMANY]
        when:
        Locale preferredSupportedLocale = resolver.findFirstPreferredSupportedLocale(userPreferredLocales)
        then:
        preferredSupportedLocale == Locale.GERMANY
    }

    void 'findPreferredSupportedLocale()'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = supportedLocales
        expect:
        preferredSupportedLocale == resolver.findFirstPreferredSupportedLocale(userPreferredLocales)
        where:
        supportedLocales                       | userPreferredLocales            | preferredSupportedLocale
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.UK, Locale.ENGLISH]     | Locale.UK    // returned first preferred locale
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.US, Locale.ENGLISH]     | Locale.US    // returned first preferred locale
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.ENGLISH]                | Locale.ENGLISH
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.CANADA, Locale.ENGLISH] | Locale.ENGLISH // returned second preferred locale
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.CANADA, Locale.US]      | Locale.US      // returned second preferred locale
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.CANADA, Locale.UK]      | Locale.UK      // returned second preferred locale
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.CANADA]                 | Locale.ENGLISH // CANADA partially supported by language ENGLISH
    }

    @Unroll('#supportedLocales, #defaultLocale, #newLocale, #preferredSupportedLocale')
    void 'setLocale() with unsupported locale should set resolved supported locale'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = supportedLocales
        resolver.defaultLocale = defaultLocale
        request.setPreferredLocales([LOCALE_FROM_USER_REQUEST])
        resolver.setLocale(request, response, newLocale)
        localeSavedToSession == request.getSession().getAttribute(SmartConfigLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)
        // reset all to null for be sure that locale was got from session
        resolver.supportedLocales = null
        resolver.defaultLocale = null
        request.setPreferredLocales([UNSUPPORTED_LOCALE])
        expect:
        preferredSupportedLocale == resolver.resolveLocale(request)
        where:
        supportedLocales                       | defaultLocale             | newLocale      | localeSavedToSession     | preferredSupportedLocale
        [Locale.ENGLISH, Locale.US, Locale.UK] | ANY_LOCALE                | Locale.UK      | Locale.UK                | Locale.UK
        [Locale.ENGLISH, Locale.US, Locale.UK] | ANY_LOCALE                | Locale.US      | Locale.US                | Locale.US
        [Locale.ENGLISH, Locale.US, Locale.UK] | ANY_LOCALE                | Locale.ENGLISH | Locale.ENGLISH           | Locale.ENGLISH
        [Locale.ENGLISH, Locale.US, Locale.UK] | ANY_LOCALE                | Locale.CANADA  | Locale.ENGLISH           | Locale.ENGLISH // newLocale partially supported by language
        [Locale.ENGLISH, Locale.US, Locale.UK] | CONFIGURED_DEFAULT_LOCALE | Locale.CANADA  | Locale.US                | Locale.US      // newLocale unsupported, returned default language
        [Locale.ENGLISH, Locale.US, Locale.UK] | null                      | Locale.CANADA  | LOCALE_FROM_USER_REQUEST | LOCALE_FROM_USER_REQUEST      // newLocale unsupported, returned default language
    }
}


