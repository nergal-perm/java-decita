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

import com.renomad.minum.web.FullSystem;
import com.renomad.minum.web.WebFramework;
import ru.ewc.checklogic.Computation;

/**
 * I am the web server for the CheckLogic application. My main responsibility is to configure the
 * routes, initialize and start the server.
 *
 * @since 0.3.0
 */
public final class WebServer {
    /**
     * The computation to be used for the web server.
     */
    private final Computation computation;

    /**
     * Ctor.
     *
     * @param computation The computation to be used for the web server.
     */
    public WebServer(final Computation computation) {
        this.computation = computation;
    }

    public void start() {
        final FullSystem minum = FullSystem.initialize();
        final WebFramework web = minum.getWebFramework();
        registerEndpoints(web, new StatePage(this.computation));
        registerEndpoints(web, new CommandPage(this.computation));
        registerEndpoints(web, new StaticResources());
        minum.block();
    }

    private static void registerEndpoints(final WebFramework web, final Endpoints endpoints) {
        endpoints.register(web);
    }
}
