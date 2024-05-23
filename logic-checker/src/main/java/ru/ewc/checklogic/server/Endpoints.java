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

import com.renomad.minum.web.RequestLine;
import com.renomad.minum.web.StatusLine;
import com.renomad.minum.web.WebFramework;
import java.util.Map;

/**
 * I am an interface for all the classes that define the endpoints for the web server.
 *
 * @since 0.3.0
 */
interface Endpoints {
    /**
     * The shortcut to define an endpoint for a GET method.
     */
    RequestLine.Method GET = RequestLine.Method.GET;

    /**
     * The shortcut to define an endpoint for a POST method.
     */
    RequestLine.Method POST = RequestLine.Method.POST;

    /**
     * Content type for CSS files.
     */
    Map<String, String> CSS = Map.of("Content-Type", "text/css");

    /**
     * Content type for plain text.
     */
    Map<String, String> PLAIN_TEXT = Map.of("Content-Type", "text/plain");

    /**
     * The status codes for the 200 OK response.
     */
    StatusLine.StatusCode OK = StatusLine.StatusCode.CODE_200_OK;

    /**
     * The status codes for the 404 Not Found response.
     */
    StatusLine.StatusCode NOT_FOUND = StatusLine.StatusCode.CODE_404_NOT_FOUND;

    /**
     * Register the endpoints with the web server.
     *
     * @param web The web framework to register endpoints in.
     */
    void register(WebFramework web);
}
