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
    void 'setLocale() should store to session only supported locale, even by language only : #newLocale, #supportedLocales: #stored by #comment'() {
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
        [ANY]             | UNSUPPORTED | null    | 'newLocale unsupported, store null'
        [ANY]             | null        | null    | 'reset stored'
    }

    @Unroll
    void 'resolveLocale(): #comment'() {
        given:
        resolver.supportedLocales = supportedLocales
        resolver.defaultLocale = defLocale
        request.setPreferredLocales(requestLocales)
        session[SmartConfigLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME] = stored
        expect:
        resolver.resolveLocale(request) == resolved
        where:
        stored | defLocale | supportedLocales | requestLocales    | resolved       | comment
        CANADA | ANY       | [ANY]            | [ANY]             | CANADA         | 'locale CANADA previously selected by user and stored in session'
        null   | DEFAULT   | null             | [UNSUPPORTED]     | DEFAULT        | 'should return default locale if user requested unsupported one'
        null   | null      | null             | [UNSUPPORTED]     | Locale.default | 'if user requested unsupported locale, and default is not set it should return system default locale'
        null   | ANY       | [US]             | [US]              | US             | 'should return user requested locale if it supported'
        null   | ANY       | [ENGLISH]        | [US]              | ENGLISH        | 'should return locale with same language if user requested locale that supported only partially by language'
        null   | ANY       | [US]             | [UNSUPPORTED, US] | US             | 'should return supported locale that is first matching with user requested locales'
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
    void 'findFirstSupportedLocale() #supportedLocales #requestLocales: #resolved by #comment'() {
        given:
        resolver.supportedLocales = supportedLocales
        expect:
        resolver.findFirstSupportedLocale(requestLocales) == resolved
        where:
        supportedLocales  | requestLocales | resolved | comment
        [ENGLISH, US, UK] | [UK]           | UK       | 'full match'
        [ENGLISH, US, UK] | [ENGLISH]      | ENGLISH  | 'full match'
        [ENGLISH]         | [CANADA]       | ENGLISH  | 'returned first locale matched by language (and actually it was lang locale itself)'
        [CANADA]          | [ENGLISH]      | CANADA   | 'returned first locale matched by language'
        [US]              | [CANADA]       | US       | 'returned first locale matched by language'
        [ENGLISH, US, UK] | [CANADA, US]   | US       | 'requested CANADA is not supported, but second requested US supported, so it returned'
        [ANY]             | [UNSUPPORTED]  | null     | 'if requested unsupported locale return null'
        [ANY]             | []             | null     | 'if no preferred locales in request return null'
    }
}


