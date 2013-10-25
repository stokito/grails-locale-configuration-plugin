package stokito

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import name.stokito.SmartConfigLocaleResolver
import spock.lang.Specification

@TestMixin(ControllerUnitTestMixin)
class SmartConfigLocaleResolverSpec extends Specification {
    static final Locale UNSUPPORTED_LOCALE = new Locale('xx')
    static final Locale ANY_LOCALE = new Locale('yy')
    static final Locale CONFIGURED_DEFAULT_LOCALE = new Locale('zz')

    void 'resolveLocale() should return user requested locale if not configured: supportedLocales is empty list'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = []
        resolver.defaultLocale = null
        request.addPreferredLocale(UNSUPPORTED_LOCALE)
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
        request.addPreferredLocale(UNSUPPORTED_LOCALE)
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
        request.addPreferredLocale(UNSUPPORTED_LOCALE)
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == CONFIGURED_DEFAULT_LOCALE
    }

    void 'resolveLocale() should return user requested locale if it supported'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [Locale.GERMANY]
        request.addPreferredLocale(Locale.GERMANY)
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == Locale.GERMANY
    }

    void 'resolveLocale() should return locale with same language if user requested locale that supported only partially by language'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [Locale.GERMAN]
        request.addPreferredLocale(Locale.GERMANY)
        assert request.locale == Locale.GERMANY
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == Locale.GERMAN
    }

    void 'findLocaleWithSameLanguage()'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [Locale.GERMAN, Locale.ITALY]
        when:
        Locale supportedLocaleWithSameLanguage = resolver.findSupportedLocaleWithSameLanguage(Locale.GERMANY)
        then:
        supportedLocaleWithSameLanguage == Locale.GERMAN
    }

    void 'resolveLocale() should return supported locale that is first matching with user requested locales'() {
        given:
        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()
        resolver.supportedLocales = [Locale.GERMANY]
        request.addPreferredLocale(UNSUPPORTED_LOCALE)
        request.addPreferredLocale(Locale.GERMANY)
        assert request.locale == UNSUPPORTED_LOCALE
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
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.UK, Locale.ENGLISH]     | Locale.UK
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.US, Locale.ENGLISH]     | Locale.US
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.ENGLISH]                | Locale.ENGLISH
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.CANADA, Locale.ENGLISH] | Locale.ENGLISH
        [Locale.ENGLISH, Locale.US, Locale.UK] | [Locale.CANADA, Locale.US]      | Locale.US
    }

}


