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

    @Unroll
    void 'resolveLocale(): #comment'() {
        given:
        resolver.supportedLocales = supportedLocales
        resolver.defaultLocale = DEFAULT
        request.preferredLocales = preferredLocales
        expect:
        resolver.resolveLocale(request) == resolved
        where:
        supportedLocales  | preferredLocales       | resolved    | comment
        []                | [UNSUPPORTED]          | UNSUPPORTED | 'should return user requested locale if not configured: supportedLocales is empty list'
        null              | [UNSUPPORTED]          | UNSUPPORTED | 'should return user requested locale if not configured: supportedLocales is null'
        null              | [UNSUPPORTED]          | DEFAULT     | 'should return default locale if user requested unsupported one'
        [GERMANY]         | [GERMANY]              | GERMANY     | 'should return user requested locale if it supported'
        [GERMAN]          | [GERMANY]              | GERMAN      | 'should return locale with same language if user requested locale that supported only partially by language'
        [GERMANY]         | [UNSUPPORTED, GERMANY] | GERMANY     | 'should return supported locale that is first matching with user requested locales'
        [ENGLISH, US, UK] | DEFAULT                | UNSUPPORTED | [] | DEFAULT | 'newLocale unsupported, returned default language'
        [ENGLISH, US, UK] | null                   | UNSUPPORTED | [] | UNSUPPORTED | 'newLocale unsupported, but default is not set, returned newLocale'
        [ENGLISH, US, UK] | ANY                    | UNSUPPORTED | [US] | US | 'from request.locales'
        [ENGLISH, US, UK] | ANY                    | UNSUPPORTED | [UK] | UK | 'from request.locales'
        [ENGLISH, US, UK] | ANY                    | UNSUPPORTED | [ENGLISH] | ENGLISH | 'from request.locales'
        [ENGLISH, US, UK] | ANY                    | UNSUPPORTED | [ENGLISH, US] | ENGLISH | 'from request.locales, selected first by priority'
        [ENGLISH, US, UK] | ANY                    | UNSUPPORTED | [CANADA] | ENGLISH | 'from request.locales, supported by language'
    }

    void 'findFirstSupportedLocaleByLanguage(): if requested locale with country but we support only language'() {
        given:
        resolver.supportedLocales = [GERMAN]
        expect:
        resolver.findFirstSupportedLocaleByLanguage([GERMANY]) == GERMAN
    }

    void 'findFirstSupportedLocaleByLanguageAndCountry()'() {
        given:
        resolver.supportedLocales = [GERMAN, GERMANY]
        expect:
        resolver.findFirstSupportedLocaleByLanguageAndCountry([GERMANY]) == GERMANY
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
    void 'setLocale() with unsupported locale should set resolved supported locale: #newLocale, #supportedLocales: resolved  #stored by #comment'() {
        given:
        resolver.supportedLocales = supportedLocales
        when:
        resolver.setLocale(request, response, newLocale)
        then:
        session[SmartConfigLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME] == stored
        where:
        supportedLocales  | newLocale   | stored  | comment
        [ENGLISH, US, UK] | UK          | UK      | 'newLocale absolutely supported, all fine'
        [ENGLISH, US, UK] | ENGLISH     | ENGLISH | 'newLocale absolutely supported, all fine'
        [ENGLISH, US, UK] | CANADA      | ENGLISH | 'newLocale partially supported by language'
        [US, UK]          | CANADA      | US      | 'newLocale partially supported by language, and selected first locale with same language'
        []                | UNSUPPORTED | null    | 'newLocale unsupported, store null'
        []                | null        | null    | 'reset stored'
    }
}


