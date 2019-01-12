/*
 * Copyright (c) 2019, Dmitriy Shchekotin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package ru.silverhammer.html.servlet;

import ru.silverhammer.core.IUiBuilder;
import ru.silverhammer.core.metadata.MetadataCollector;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.core.string.SimpleStringProcessor;
import ru.silverhammer.demo.settings.Environment;
import ru.silverhammer.demo.settings.Settings;
import ru.silverhammer.demo.user.User;
import ru.silverhammer.html.HtmlControlResolver;
import ru.silverhammer.html.HtmlUiBuilder;
import ru.silverhammer.injection.IInjector;
import ru.silverhammer.injection.Injector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/View")
public class View extends HttpServlet {

    private static final long serialVersionUID = -6539765437025975167L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Environment env = new Environment();
        Settings settings = new Settings();
        User user = new User();

        PrintWriter out = resp.getWriter();
        out.println(generate(new SimpleStringProcessor(), user, env, settings));
    }

    protected String generate(IStringProcessor stringProcessor, Object... data) {
        IInjector injector = new Injector();
        MetadataCollector collector = new MetadataCollector(new HtmlControlResolver(), stringProcessor, injector);
        UiMetadata metadata = collector.collect(data);
        HtmlUiBuilder builder = new HtmlUiBuilder();
        injector.bind(IUiBuilder.class, builder);
        return builder.buildUi(metadata);
    }
}
