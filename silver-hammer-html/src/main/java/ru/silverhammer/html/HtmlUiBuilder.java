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
package ru.silverhammer.html;

import ru.silverhammer.core.IUiBuilder;
import ru.silverhammer.core.metadata.CategoryAttributes;
import ru.silverhammer.core.metadata.ControlAttributes;
import ru.silverhammer.core.metadata.GroupAttributes;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.html.control.Control;

public class HtmlUiBuilder implements IUiBuilder<String> {

    @Override
    public String buildUi(UiMetadata metadata) {
        StringBuilder builder = new StringBuilder();
        if (metadata.hasCategories()) {
            for (CategoryAttributes ca : metadata.getCategories()) {
                if (!ca.isEmpty()) {
                    createGroups(ca, builder);
                }
            }
        } else {
            createGroups(metadata.getGroups(), builder);
        }
        return builder.toString();
    }

    private void createGroups(Iterable<GroupAttributes> groups, StringBuilder builder) {
        for (GroupAttributes ga : groups) {
            if (!ga.isEmpty()) {
                for (ControlAttributes ca : ga) {
                    placeControl(ca, builder);
                }
            }
        }
    }

    private void placeControl(ControlAttributes attributes, StringBuilder builder) {
        if (attributes.getCaption() != null) {
            builder.append(attributes.getCaption());
            builder.append(((Control<?, ?>) attributes.getControl()).render(attributes.getCaption()));
        }
    }

    @Override
    public boolean showDialog(String title, UiMetadata metadata) {
        return false;
    }
}
