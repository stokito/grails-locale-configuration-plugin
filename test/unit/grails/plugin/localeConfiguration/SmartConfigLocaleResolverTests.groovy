/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugin.localeConfiguration

import junit.framework.TestCase
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import javax.servlet.http.HttpSession

/**
 * SmartConfigLocaleResolver extends SessionLocaleResolverTests so it should keep contract of superclass.
 * This is renamed SessionLocaleResolverTests that uses SmartConfigLocaleResolver instead of original SessionLocaleResolverTests.
 * @author Juergen Hoeller
 */
public class SmartConfigLocaleResolverTests extends TestCase {

    public void testResolveLocale() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(SmartConfigLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.GERMAN);

        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver();
        assertEquals(Locale.GERMAN, resolver.resolveLocale(request));
    }

    public void testSetAndResolveLocale() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver();
        resolver.setLocale(request, response, Locale.GERMAN);
        assertEquals(Locale.GERMAN, resolver.resolveLocale(request));

        HttpSession session = request.getSession();
        request = new MockHttpServletRequest();
        request.setSession(session);
        resolver = new SmartConfigLocaleResolver();

        assertEquals(Locale.GERMAN, resolver.resolveLocale(request));
    }

    public void testResolveLocaleWithoutSession() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);

        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver();

        assertEquals(request.getLocale(), resolver.resolveLocale(request));
    }

    public void testResolveLocaleWithoutSessionAndDefaultLocale() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);

        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver();
        resolver.setDefaultLocale(Locale.GERMAN);

        assertEquals(Locale.GERMAN, resolver.resolveLocale(request));
    }

    public void testSetLocaleToNullLocale() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        request.getSession().setAttribute(SmartConfigLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.GERMAN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        SmartConfigLocaleResolver resolver = new SmartConfigLocaleResolver();
        resolver.setLocale(request, response, null);
        Locale locale = (Locale) request.getSession().getAttribute(SmartConfigLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        assertNull(locale);

        HttpSession session = request.getSession();
        request = new MockHttpServletRequest();
        request.addPreferredLocale(Locale.TAIWAN);
        request.setSession(session);
        resolver = new SmartConfigLocaleResolver();
        assertEquals(Locale.TAIWAN, resolver.resolveLocale(request));
    }

}