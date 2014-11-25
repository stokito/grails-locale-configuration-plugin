package grails.plugin.localeConfiguration

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

import static java.util.Locale.*

@TestMixin(ControllerUnitTestMixin)
class SmartConfigLocaleResolverSpec extends Specification {
    static final UNSUPPORTED = new Locale('unsupported')
    static final ANY = new Locale('any')
    static final DEFAULT = new Locale('default')
    SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver()

    void 'resolveLocale() should return user requested locale if not configured: supportedLocales is empty list'() {
        given:
        resolver.supportedLocales = []
        request.preferredLocales = [UNSUPPORTED]
        when:
        Locale resolved = resolver.resolveLocale(request)
        then:
        resolved == UNSUPPORTED
    }

    void 'resolveLocale() should return user requested locale if not configured: supportedLocales is null'() {
        given:
        resolver.supportedLocales = null
        request.preferredLocales = [UNSUPPORTED]
        when:
        Locale resolved = resolver.resolveLocale(request)
        then:
        resolved == UNSUPPORTED
    }

    void 'resolveLocale() should return default locale if user requested unsupported one'() {
        given:
        resolver.supportedLocales = null
        resolver.defaultLocale = DEFAULT
        request.preferredLocales = [UNSUPPORTED]
        when:
        Locale resolved = resolver.resolveLocale(request)
        then:
        resolved == DEFAULT
    }

    void 'resolveLocale() should return user requested locale if it supported'() {
        given:
        resolver.supportedLocales = [GERMANY]
        request.preferredLocales = [GERMANY]
        when:
        Locale resolved = resolver.resolveLocale(request)
        then:
        resolved == GERMANY
    }

    void 'resolveLocale() should return locale with same language if user requested locale that supported only partially by language'() {
        given:
        resolver.supportedLocales = [GERMAN]
        request.preferredLocales = [GERMANY]
        when:
        Locale resolved = resolver.resolveLocale(request)
        then:
        resolved == GERMAN
    }

    void 'resolveLocale() should return supported locale that is first matching with user requested locales'() {
        given:
        resolver.supportedLocales = [GERMANY]
        request.preferredLocales = [UNSUPPORTED, GERMANY]
        when:
        Locale resolved = resolver.resolveLocale(request)
        then:
        resolved == GERMANY
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
        resolver.supportedLocales = [GERMAN, GERMANY, ANY]
        when:
        Locale resolved = resolver.findFirstSupportedLocaleByLanguageAndCountry([GERMANY])
        then:
        resolved == GERMANY
    }

    @Unroll
    void 'findFirstSupportedLocale(): #supportedLocales #requestLocales: resolved by #comment'() {
        given:
        resolver.supportedLocales = supportedLocales
        expect:
        resolver.findFirstSupportedLocale(requestLocales) == resolved
        where:
        supportedLocales  | requestLocales    | resolved | comment
        [ENGLISH, US, UK] | [UK, ENGLISH]     | UK       | 'returned first preferred locale'
        [ENGLISH, US, UK] | [US, ENGLISH]     | US       | 'returned first preferred locale'
        [ENGLISH, US, UK] | [ENGLISH]         | ENGLISH  | 'full match'
        [ENGLISH, US, UK] | [CANADA, ENGLISH] | ENGLISH  | 'returned second preferred locale'
        [ENGLISH, US, UK] | [CANADA, US]      | US       | 'returned second preferred locale'
        [ENGLISH, US, UK] | [CANADA, UK]      | UK       | 'returned second preferred locale'
        [ENGLISH, US, UK] | [CANADA]          | ENGLISH  | 'CANADA partially supported by language ENGLISH'
        // dd
        [FRENCH] | [FRANCE] | FRENCH | ''
        [FRENCH] | [FRANCE] | FRENCH | ''
        [FRANCE] | [FRANCE] | FRANCE | ''
        // ddd
        [GERMANY] | [UNSUPPORTED, GERMANY] | GERMANY | 'should return supported locale that is first matching with user requested locales'
    }

    @Unroll
    void 'setLocale() with unsupported locale should set resolved supported locale: #defLocale, #newLocale, #requestLocales: resolved by #comment'() {
        given:
        resolver.supportedLocales = supportedLocales
        resolver.defaultLocale = defLocale
        request.preferredLocales = requestLocales
        when:
        resolver.setLocale(request, response, newLocale)
        Locale localeSavedToSession = (Locale) session[SmartConfigLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME]
        // reset all to null to ensure that locale was got from session
        resolver.supportedLocales = null
        resolver.defaultLocale = null
        request.preferredLocales = [ANY]
        then:
        localeSavedToSession == resolved
        resolved == resolver.resolveLocale(request)
        where:
        supportedLocales  | defLocale | newLocale   | requestLocales | resolved    | comment
        [ENGLISH, US, UK] | ANY       | UK          | [ANY]          | UK          | 'newLocale absolutely supported, all fine'
        [ENGLISH, US, UK] | ANY       | US          | [ANY]          | US          | 'newLocale absolutely supported, all fine'
        [ENGLISH, US, UK] | ANY       | ENGLISH     | [ANY]          | ENGLISH     | 'newLocale absolutely supported, all fine'
        [ENGLISH, US, UK] | ANY       | CANADA      | [ANY]          | ENGLISH     | 'newLocale partially supported by language'
        [ENGLISH, US, UK] | DEFAULT   | UNSUPPORTED | [ANY]          | DEFAULT     | 'newLocale unsupported, returned default language'
        [ENGLISH, US, UK] | null      | UNSUPPORTED | [ANY]          | UNSUPPORTED | 'newLocale unsupported, but default is not set, returned newLocale'
        [ENGLISH, US, UK] | ANY       | UNSUPPORTED | [US]           | US          | 'from request.locales'
        [ENGLISH, US, UK] | ANY       | UNSUPPORTED | [UK]           | UK          | 'from request.locales'
        [ENGLISH, US, UK] | ANY       | UNSUPPORTED | [ENGLISH]      | ENGLISH     | 'from request.locales'
        [ENGLISH, US, UK] | ANY       | UNSUPPORTED | [ENGLISH, US]  | ENGLISH     | 'from request.locales, selected first by priority'
        [ENGLISH, US, UK] | ANY       | UNSUPPORTED | [CANADA]       | ENGLISH     | 'from request.locales, supported by language'
    }
}


