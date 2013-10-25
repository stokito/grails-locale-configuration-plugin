package stokito

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import name.stokito.SmartConfigLocaleResolver
import spock.lang.Specification

@TestMixin(ControllerUnitTestMixin)
class SmartConfigLocaleResolverSpec extends Specification {
    static final Locale UNSUPPORTED_LOCALE = new Locale('xx')

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


}


