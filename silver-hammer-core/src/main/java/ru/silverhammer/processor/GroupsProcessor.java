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
package ru.silverhammer.processor;

import ru.silverhammer.conversion.IStringConverter;
import ru.silverhammer.model.GroupModel;
import ru.silverhammer.model.UiModel;
import ru.silverhammer.reflection.ClassReflection;

import java.lang.annotation.Annotation;
import java.util.Objects;

// TODO: consider adding groups based on their occurrence in class fields
public class GroupsProcessor implements IProcessor<ClassReflection<?>, Annotation> {

    protected final IStringConverter converter;
    protected final UiModel model;

    public GroupsProcessor(IStringConverter converter, UiModel model) {
        this.converter = converter;
        this.model = model;
    }

    @Override
    public void process(Object data, ClassReflection<?> reflection, Annotation annotation) {
        if (annotation instanceof Groups) {
            Groups groups = (Groups) annotation;
            for (Groups.Group group : groups.value()) {
                process(data, reflection, group);
            }
        } else if (annotation instanceof Groups.Group) {
            Groups.Group group = (Groups.Group) annotation;
            if (model.findGroupModel(g -> Objects.equals(g.getId(), group.value())) == null) {
                GroupModel groupModel = createGroupModel(group);
                if (model.getCategories().isEmpty()) {
                    model.getGroups().add(groupModel);
                } else {
                    model.getCategories().get(0).getGroups().add(groupModel);
                }
            }
        }
    }

    protected GroupModel createGroupModel(Groups.Group group) {
        GroupModel result = new GroupModel(group.value());
        result.setCaption(group.caption().trim().length() > 0 ? converter.getString(group.caption()) : null);
        return result;
    }
}
