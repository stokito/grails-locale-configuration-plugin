//package name.stokito

/*
 * From Coeus web framework
 * Licensed under the Apache License, Version 2.0.
 * Author: Spiros Tzavellas
 */
/*
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.web.MockHttpServletRequest

class SessionLocaleResolverTest {

    val defaultLocale = Locale.UK
    val resolver = new SessionLocaleResolver(Some(defaultLocale))
    val request = new MockHttpServletRequest
    val response = null


    @Test
    def use_the_default_locale_if_no_locale_in_session() {
        assertEquals(defaultLocale, resolver.resolve(request))
    }

    @Test
    def use_request_locale_if_no_locale_in_session_and_default_not_specified() {
        val noDefault = new SessionLocaleResolver
        request.addPreferredLocale(Locale.US)
        assertEquals(Locale.US, noDefault.resolve(request))
    }

    @Test
    def set_the_locale_to_sesion() {
        resolver.setLocale(request, response, Locale.US)
        assertEquals(Locale.US, resolver.resolve(request))
    }

    @Test
    def setting_the_locale_to_null_resets_to_default_locale() {
        resolver.setLocale(request, response, Locale.US)
        assertEquals(Locale.US, resolver.resolve(request))

        resolver.setLocale(request, response, null)
        assertEquals(defaultLocale, resolver.resolve(request))
    }
}
*/
