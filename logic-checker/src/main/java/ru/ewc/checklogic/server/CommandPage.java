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
import ru.ewc.checklogic.Transition;
import ru.ewc.state.StoredState;

/**
 * I am a configuration object for all the command-related endpoints.
 *
 * @since 0.3.0
 */
public final class CommandPage implements Endpoints {
    /**
     * The computation to be used for the command processing.
     */
    private final Computation computation;

    /**
     * The template processor for the Command page.
     */
    private final TemplateProcessor template;

    /**
     * Ctor.
     *
     * @param computation The computation to be used for the command processing.
     */
    public CommandPage(final Computation computation) {
        this.computation = computation;
        this.template = TemplateProcessor.buildProcessor(
            WebResource.readFileFromResources("templates/command-info.html")
        );
    }

    @Override
    public void register(final WebFramework web) {
        web.registerPath(POST, "command", this::executeCommand);
        web.registerPath(GET, "command", this::commandInfo);
    }

    public Response executeCommand(final Request request) {
        final String command = request.body().asString("command");
        this.computation.perform(new Transition(command, new StoredState(Map.of())));
        return Response.htmlOk("OK", Map.of("HX-Redirect", "/"));
    }

    public Response commandInfo(final Request request) {
        final String command = request.requestLine().queryString().getOrDefault("command", "");
        return Response.htmlOk(this.template.renderTemplate(Map.of("command_name", command)));
    }
}
