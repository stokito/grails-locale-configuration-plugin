package grails.plugin.localeConfiguration

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

import static java.util.Locale.*

@TestMixin(ControllerUnitTestMixin)
class SmartConfigLocaleResolverSpec extends Specification {
    static final UNSUPPORTED_LOCALE = new Locale('unsupported')
    static final ANY_LOCALE = new Locale('any')
    static final CONFIGURED_DEFAULT_LOCALE = new Locale('configured_default')
    SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()

    void 'resolveLocale() should return user requested locale if not configured: supportedLocales is empty list'() {
        given:
        resolver.supportedLocales = []
        resolver.defaultLocale = null
        request.preferredLocales = [UNSUPPORTED_LOCALE]
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == UNSUPPORTED_LOCALE
    }

    void 'resolveLocale() should return user requested locale if not configured: supportedLocales is null'() {
        given:
        resolver.supportedLocales = null
        resolver.defaultLocale = null
        request.preferredLocales = [UNSUPPORTED_LOCALE]
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == UNSUPPORTED_LOCALE
    }

    void 'resolveLocale() should return default locale if user requested unsupported one'() {
        given:
        resolver.supportedLocales = null
        resolver.defaultLocale = CONFIGURED_DEFAULT_LOCALE
        request.preferredLocales = [UNSUPPORTED_LOCALE]
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == CONFIGURED_DEFAULT_LOCALE
    }

    void 'resolveLocale() should return user requested locale if it supported'() {
        given:
        resolver.supportedLocales = [GERMANY]
        request.preferredLocales = [GERMANY]
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == GERMANY
    }

    void 'resolveLocale() should return locale with same language if user requested locale that supported only partially by language'() {
        given:
        resolver.supportedLocales = [GERMAN]
        request.preferredLocales = [GERMANY]
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == GERMAN
    }

    void 'findFirstSupportedLocaleByLanguage(): if requested locale with country but we support only language'() {
        given:
        resolver.supportedLocales = [GERMAN]
        when:
        Locale supportedLocaleWithSameLanguage = resolver.findFirstSupportedLocaleByLanguage([GERMANY])
        then:
        supportedLocaleWithSameLanguage == GERMAN
    }

    void 'findFirstSupportedLocaleByLanguageAndCountry()'() {
        given:
        resolver.supportedLocales = [GERMAN, GERMANY, ANY_LOCALE]
        when:
        Locale supportedLocaleWithSameLanguage = resolver.findFirstSupportedLocaleByLanguageAndCountry([GERMANY])
        then:
        supportedLocaleWithSameLanguage == GERMANY
    }

    void 'resolveLocale() should return supported locale that is first matching with user requested locales'() {
        given:
        resolver.supportedLocales = [GERMANY]
        request.preferredLocales = [UNSUPPORTED_LOCALE, GERMANY]
        when:
        Locale resolvedLocale = resolver.resolveLocale(request)
        then:
        resolvedLocale == GERMANY
    }

    void 'findFirstSupportedLocale() should return supported locale that is first matching with user requested locales'() {
        given:
        resolver.supportedLocales = [GERMANY]
        List<Locale> requestLocales = [UNSUPPORTED_LOCALE, GERMANY]
        when:
        Locale preferredSupportedLocale = resolver.findFirstSupportedLocale(requestLocales)
        then:
        preferredSupportedLocale == GERMANY
    }

    @Unroll
    void 'findFirstSupportedLocale(): #supportedLocales, #requestLocales: resolved by #comment'() {
        given:
        resolver.supportedLocales = supportedLocales
        expect:
        resolver.findFirstSupportedLocale(requestLocales) == preferredSupportedLocale
        where:
        supportedLocales  | requestLocales    | preferredSupportedLocale | comment
        [ENGLISH, US, UK] | [UK, ENGLISH]     | UK                       | 'returned first preferred locale'
        [ENGLISH, US, UK] | [US, ENGLISH]     | US                       | 'returned first preferred locale'
        [ENGLISH, US, UK] | [ENGLISH]         | ENGLISH                  | 'full match'
        [ENGLISH, US, UK] | [CANADA, ENGLISH] | ENGLISH                  | 'returned second preferred locale'
        [ENGLISH, US, UK] | [CANADA, US]      | US                       | 'returned second preferred locale'
        [ENGLISH, US, UK] | [CANADA, UK]      | UK                       | 'returned second preferred locale'
        [ENGLISH, US, UK] | [CANADA]          | ENGLISH                  | 'CANADA partially supported by language ENGLISH'
    }

    @Unroll
    void 'findFirstSupportedLocale(): #mainRequestedLocale #supportedLocales #bestLocale'() {
        given:
        resolver.supportedLocales = supportedLocales
        expect:
        resolver.findFirstSupportedLocale(requestedLocales) == bestLocale
        where:
        requestedLocales | supportedLocales | bestLocale
        [FRANCE]         | [FRENCH]         | FRENCH
        [FRANCE]         | [FRENCH]         | FRENCH
        [FRANCE]         | [FRANCE]         | FRANCE
    }

    @Unroll
    void 'setLocale() with unsupported locale should set resolved supported locale: #configuredDefaultLocale, #newLocale, #requestLocales: resolved by #comment'() {
        given:
        resolver.supportedLocales = configuredSupportedLocales
        resolver.defaultLocale = configuredDefaultLocale
        request.preferredLocales = requestLocales
        when:
        resolver.setLocale(request, response, newLocale)
        Locale localeSavedToSession = (Locale) session[SmartConfigLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME]
        // reset all to null to ensure that locale was got from session
        resolver.supportedLocales = null
        resolver.defaultLocale = null
        request.preferredLocales = [ANY_LOCALE]
        then:
        localeSavedToSession == preferredSupportedLocale
        preferredSupportedLocale == resolver.resolveLocale(request)
        where:
        configuredSupportedLocales | configuredDefaultLocale   | newLocale          | requestLocales | preferredSupportedLocale  | comment
        [ENGLISH, US, UK]          | ANY_LOCALE                | UK                 | [ANY_LOCALE]   | UK                        | 'newLocale absolutely supported, all fine'
        [ENGLISH, US, UK]          | ANY_LOCALE                | US                 | [ANY_LOCALE]   | US                        | 'newLocale absolutely supported, all fine'
        [ENGLISH, US, UK]          | ANY_LOCALE                | ENGLISH            | [ANY_LOCALE]   | ENGLISH                   | 'newLocale absolutely supported, all fine'
        [ENGLISH, US, UK]          | ANY_LOCALE                | CANADA             | [ANY_LOCALE]   | ENGLISH                   | 'newLocale partially supported by language'
        [ENGLISH, US, UK]          | CONFIGURED_DEFAULT_LOCALE | UNSUPPORTED_LOCALE | [ANY_LOCALE]   | CONFIGURED_DEFAULT_LOCALE | 'newLocale unsupported, returned default language'
        [ENGLISH, US, UK]          | null                      | UNSUPPORTED_LOCALE | [ANY_LOCALE]   | UNSUPPORTED_LOCALE        | 'newLocale unsupported, but default is not set, returned newLocale'
        [ENGLISH, US, UK]          | ANY_LOCALE                | UNSUPPORTED_LOCALE | [US]           | US                        | 'from request.locales'
        [ENGLISH, US, UK]          | ANY_LOCALE                | UNSUPPORTED_LOCALE | [UK]           | UK                        | 'from request.locales'
        [ENGLISH, US, UK]          | ANY_LOCALE                | UNSUPPORTED_LOCALE | [ENGLISH]      | ENGLISH                   | 'from request.locales'
        [ENGLISH, US, UK]          | ANY_LOCALE                | UNSUPPORTED_LOCALE | [ENGLISH, US]  | ENGLISH                   | 'from request.locales, selected first by priority'
        [ENGLISH, US, UK]          | ANY_LOCALE                | UNSUPPORTED_LOCALE | [CANADA]       | ENGLISH                   | 'from request.locales, supported by language'
    }
}


