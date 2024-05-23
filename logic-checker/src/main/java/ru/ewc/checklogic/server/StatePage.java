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

import com.renomad.minum.templating.TemplateProcessor;
import com.renomad.minum.web.Request;
import com.renomad.minum.web.Response;
import com.renomad.minum.web.WebFramework;
import java.util.Map;
import ru.ewc.checklogic.Computation;

/**
 * I am the configuration and logic for the state web page.
 *
 * @since 0.3.0
 */
public final class StatePage implements Endpoints {
    /**
     * The template processor for the State page.
     */
    private final TemplateProcessor template;

    /**
     * An instance of an object tracking the application state.
     */
    private final Computation computation;

    public StatePage(final Computation computation) {
        this.computation = computation;
        this.template = TemplateProcessor.buildProcessor(
            WebResource.readFileFromResources("templates/state.html")
        );
    }

    @Override
    public void register(final WebFramework web) {
        web.registerPath(GET, "", this::statePage);
    }

    // @todo #9 Output all the commands as buttons on the page
    public Response statePage(final Request request) {
        final StoredState stored = new StoredState(this.computation.storedState());
        return Response.htmlOk(this.template.renderTemplate(Map.of("state", stored.asHtmlList())));
    }
}
