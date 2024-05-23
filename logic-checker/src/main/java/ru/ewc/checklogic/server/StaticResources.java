/*
 * MIT License
 *
 * Copyright (c) 2024 Eugene Terekhov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.ewc.checklogic.server;

import com.renomad.minum.web.Request;
import com.renomad.minum.web.Response;
import com.renomad.minum.web.WebFramework;

/**
 * I am a class providing access to static files, packed inside a jar.
 *
 * @since 0.3.0
 */
public final class StaticResources implements Endpoints {
    @Override
    public void register(final WebFramework web) {
        web.registerPartialPath(GET, "static", StaticResources::staticResource);
    }

    private static Response staticResource(final Request request) {
        final Response result;
        if (request.requestLine().getPathDetails().isolatedPath().endsWith("main.css")) {
            final String body = WebResource.readFileFromResources("static/main.css");
            result = new Response(OK, body, CSS);
        } else {
            result = new Response(NOT_FOUND, "", PLAIN_TEXT);
        }
        return result;
    }
}
